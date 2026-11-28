package cr.ac.tec.pokerhorse.core.engine

import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.betting.*
import cr.ac.tec.pokerhorse.core.variants.*
import kotlin.random.Random

class MotorDeJuego internal constructor(
    private val obtenerReglas: (Variante) -> ReglasVariante,
    private val repartidor: Repartidor,
    private val apuestas: GestorApuestas,
    private val botes: CalculadorDeBotes
) {
    constructor() : this(Variantes::obtener, Repartidor(), GestorApuestas(), CalculadorDeBotes())

    private val inicio = InicioMano(repartidor)
    private val rotacion = listOf(Variante.HOLD_EM, Variante.OMAHA_HI_LO,
        Variante.RAZZ, Variante.STUD, Variante.STUD_HI_LO)

    fun iniciar(configuracion: ConfiguracionPartida, nombres: List<String>,
                mazo: Mazo = Mazo.crear().mezclar(Random.Default)): EstadoMotor {
        require(nombres.size == configuracion.cantidadJugadores)
        val jugadores = nombres.mapIndexed { i, nombre ->
            Jugador(JugadorId(i), nombre, Asiento(i), configuracion.fichasIniciales)
        }
        val estado = inicio.iniciar(configuracion, jugadores, Asiento(0), mazo,
            obtenerReglas(rotacion.first()), 0)
        return continuar(estado, null)
    }

    fun accionesLegales(estado: EstadoMotor): List<Accion> {
        val ronda = estado.ronda ?: return emptyList()
        return apuestas.legales(estado.mano, ronda)
    }

    fun aplicar(estado: EstadoMotor, actor: JugadorId, accion: Accion): ResultadoTransicion<EstadoMotor> {
        fun rechazo(error: ErrorDominio) = ResultadoTransicion.Rechazo(error)
        if (estado.finalizada) return rechazo(ErrorDominio.MANO_FINALIZADA)
        if (estado.mano.buscarJugador(actor) == null) return rechazo(ErrorDominio.JUGADOR_INEXISTENTE)
        if (actor !in estado.mano.activos()) return rechazo(ErrorDominio.JUGADOR_NO_ACTIVO)
        if (actor != estado.mano.turno) return rechazo(ErrorDominio.FUERA_DE_TURNO)
        if (accion !in accionesLegales(estado)) return rechazo(ErrorDominio.ACCION_NO_PERMITIDA)
        val (mano, ronda) = apuestas.aplicar(estado.mano, estado.ronda!!, accion)
        val intermedio = EstadoMotor(estado.partida.copy(manoActual = mano), ronda)
        return ResultadoTransicion.Exito(continuar(intermedio, actor))
    }

    private fun continuar(estado: EstadoMotor, ultimoActor: JugadorId?): EstadoMotor {
        var mano = estado.mano.reconstruir(turno = null)
        var ronda = estado.ronda!!
        var ultimo = ultimoActor
        val config = estado.partida.configuracion
        val reglas = obtenerReglas(mano.variante)
        while (true) {
            val contendientes = mano.contendientes()
            if (contendientes.size == 1) return liquidar(estado.partida, mano, reglas)
            val activos = mano.activos()
            if (activos.size == 1) {
                // No se puede apostar contra nadie que pueda igualar.
                // El último activo sí debe responder a un all-in pendiente.
                val id = activos.single()
                val objetivoReal = mano.obtenerParticipaciones()
                    .filter { it.jugadorId != id }.maxOfOrNull { it.aporteCalle } ?: 0L
                val pendiente = mano.buscarParticipacion(id)!!.aporteCalle < objetivoReal
                ronda = ronda.copy(objetivo = objetivoReal,
                    pendientes = if (pendiente) setOf(id) else emptySet(),
                    derechoSubida = emptySet())
            }
            val pendientes = ronda.pendientes.intersect(activos)
            if (pendientes.isNotEmpty()) {
                val indice = ronda.orden.indexOfFirst { it == ultimo }
                val orden = if (indice < 0) ronda.orden else ronda.orden.drop(indice + 1) + ronda.orden.take(indice + 1)
                val turno = orden.first { it in pendientes }
                mano = mano.reconstruir(turno = turno)
                return EstadoMotor(estado.partida.copy(manoActual = mano), ronda.copy(pendientes = pendientes))
            }

            mano = botes.devolverExcedente(mano)
            val siguiente = reglas.calles.getOrNull(reglas.calles.indexOf(mano.calle) + 1)
                ?: return liquidar(estado.partida, mano, reglas)
            mano = mano.reconstruir(participaciones = mano.obtenerParticipaciones().map {
                it.reiniciarAporteCalle()
            }, aumentos = 0)
            mano = repartidor.repartir(mano, siguiente, reglas)
            val orden = reglas.ordenAccion(mano)
            val nuevosActivos = mano.activos()
            ronda = RondaApuestas(orden, nuevosActivos, nuevosActivos, 0,
                reglas.incrementosPermitidos(mano, config), false, reglas.maximoAumentosPorCalle)
            ultimo = null
        }
    }

    private fun liquidar(partida: EstadoPartida, mano: EstadoMano, reglas: ReglasVariante): EstadoMotor {
        val normal = botes.devolverExcedente(mano)
        val premios = botes.distribuir(normal, reglas)
        val porJugador = premios.groupBy { it.jugadorId }.mapValues { (_, p) -> p.sumOf { it.cantidad } }
        val jugadores = normal.obtenerJugadores().map { it.recibirFichas(porJugador[it.id] ?: 0L) }
        val participaciones = normal.obtenerParticipaciones().map {
            ParticipacionMano(it.jugadorId, it.obtenerCartas(), estado = it.estado)
        }
        val final = normal.reconstruir(jugadores = jugadores, participaciones = participaciones,
            turno = null, aumentos = 0)
        return EstadoMotor(partida.copy(manoActual = final,
            manosCompletadas = Math.addExact(partida.manosCompletadas, 1L)), null, ResultadoMano(premios))
    }

    fun siguienteMano(estado: EstadoMotor, mazo: Mazo = Mazo.crear().mezclar(Random.Default)):
        ResultadoTransicion<EstadoMotor> {
        if (!estado.finalizada) return ResultadoTransicion.Rechazo(ErrorDominio.ACCION_NO_PERMITIDA)
        val jugadores = estado.mano.obtenerJugadores()
        val vivos = jugadores.filter { it.fichas > 0 }.sortedBy { it.asiento.posicion }
        if (vivos.size < 2) return ResultadoTransicion.Rechazo(ErrorDominio.PARTIDA_FINALIZADA)
        val boton = (vivos.firstOrNull { it.asiento.posicion > estado.mano.boton.posicion }
            ?: vivos.first()).asiento
        val partida = estado.partida
        val indice = ((partida.manosCompletadas / partida.configuracion.manosPorVariante) % rotacion.size).toInt()
        val nueva = inicio.iniciar(partida.configuracion, jugadores, boton, mazo,
            obtenerReglas(rotacion[indice]), partida.manosCompletadas)
        return ResultadoTransicion.Exito(continuar(nueva, null))
    }

    fun vistaPara(estado: EstadoMotor, jugadorId: JugadorId): VistaJugador {
        val mano = estado.mano
        require(mano.buscarJugador(jugadorId) != null)
        val reglas = obtenerReglas(mano.variante)
        val participantes = mano.obtenerJugadores().map { jugador ->
            val p = mano.buscarParticipacion(jugador.id)
            VistaParticipante(jugador.id, jugador.nombre, jugador.asiento, jugador.fichas,
                p?.aporteCalle ?: 0L, p?.aporteTotal ?: 0L, p?.estado,
                reglas.cartasVisibles(p?.obtenerCartas().orEmpty()))
        }
        return VistaJugador(jugadorId, mano.variante, mano.calle, mano.boton, mano.turno,
            mano.pozo, participantes, mano.buscarParticipacion(jugadorId)?.obtenerCartas().orEmpty(),
            mano.obtenerComunitarias(),
            if (mano.turno == jugadorId) accionesLegales(estado) else emptyList(),
            estado.finalizada, estado.resultado?.premios.orEmpty(), estado.partida.manosCompletadas)
    }
}
