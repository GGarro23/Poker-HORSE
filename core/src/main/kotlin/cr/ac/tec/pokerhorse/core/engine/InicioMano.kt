package cr.ac.tec.pokerhorse.core.engine

import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.variants.ReglasVariante

internal class InicioMano(private val repartidor: Repartidor = Repartidor()) {
    fun iniciar(config: ConfiguracionPartida, jugadores: List<Jugador>, boton: Asiento,
                mazo: Mazo, reglas: ReglasVariante, completadas: Long): EstadoMotor {
        require(mazo.cantidad == 52)
        require(jugadores.count { it.fichas > 0 } >= 2)
        val participantes = jugadores.filter { it.fichas > 0 }.map { ParticipacionMano(it.id) }
        var mano = EstadoMano(reglas.variante, reglas.calles.first(), mazo, jugadores,
            participantes, boton = boton)
        mano = repartidor.repartir(mano, mano.calle, reglas)
        val obligaciones = reglas.apuestasForzosas(mano, config)
        for (obligacion in obligaciones) {
            val jugador = mano.buscarJugador(obligacion.jugadorId)!!
            val participacion = mano.buscarParticipacion(jugador.id)!!
            val monto = minOf(jugador.fichas, obligacion.cantidad)
            if (monto == 0L) continue
            val aporte = participacion.aportarFichas(jugador, monto, obligacion.esAnte)
            mano = mano.reconstruir(
                jugadores = mano.obtenerJugadores().map { if (it.id == jugador.id) aporte.jugador else it },
                participaciones = mano.obtenerParticipaciones().map {
                    if (it.jugadorId == jugador.id) aporte.participacion else it
                })
        }
        val forzosas = obligaciones.filterNot { it.esAnte }
        val base = reglas.apuestaBase(mano.calle, config)
        // Dos ciegas constituyen apertura; una obligación es bring-in,
        // que puede completarse a la apuesta base sin consumir una subida.
        val abierta = forzosas.size >= 2 || (forzosas.size == 1 && mano.apuestaVigente >= base)
        val objetivo = if (forzosas.size >= 2) base else mano.apuestaVigente
        var orden = reglas.ordenAccion(mano)
        if (forzosas.size == 1) {
            val asiento = mano.buscarJugador(forzosas.single().jugadorId)!!.asiento.posicion
            val ids = mano.obtenerJugadores().filter { it.id in mano.activos() }.sortedBy { it.asiento.posicion }
            orden = (ids.filter { it.asiento.posicion > asiento } +
                ids.filter { it.asiento.posicion <= asiento }).map { it.id }
        }
        val activos = mano.activos()
        val ronda = RondaApuestas(orden, activos, activos, objetivo,
            reglas.incrementosPermitidos(mano, config), abierta, reglas.maximoAumentosPorCalle)
        mano = mano.reconstruir(turno = orden.firstOrNull())
        return EstadoMotor(EstadoPartida(config, mano, completadas), ronda)
    }
}
