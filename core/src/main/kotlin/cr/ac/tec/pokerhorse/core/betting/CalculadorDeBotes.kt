package cr.ac.tec.pokerhorse.core.betting

import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.engine.*
import cr.ac.tec.pokerhorse.core.variants.*

internal data class Bote(val cantidad: Long, val elegibles: Set<JugadorId>)

internal class CalculadorDeBotes {
    /** El excedente no igualado se devuelve al cerrar cada calle. */
    fun devolverExcedente(mano: EstadoMano): EstadoMano {
        val orden = mano.obtenerParticipaciones().sortedByDescending { it.aporteCalle }
        val mayor = orden.firstOrNull() ?: return mano
        val segundo = orden.getOrNull(1)?.aporteCalle ?: 0L
        val excedente = mayor.aporteCalle - segundo
        if (excedente == 0L) return mano
        val jugador = mano.buscarJugador(mayor.jugadorId)!!
        val estado = if (mayor.estado == EstadoJugador.ALL_IN) EstadoJugador.ACTIVO else mayor.estado
        val participacion = ParticipacionMano(mayor.jugadorId, mayor.obtenerCartas(),
            mayor.aporteCalle - excedente, mayor.aporteTotal - excedente, estado)
        return mano.reconstruir(
            jugadores = mano.obtenerJugadores().map { if (it.id == jugador.id) it.recibirFichas(excedente) else it },
            participaciones = mano.obtenerParticipaciones().map { if (it.jugadorId == jugador.id) participacion else it },
            turno = null)
    }

    fun construir(mano: EstadoMano): List<Bote> {
        val participaciones = mano.obtenerParticipaciones()
        val contendientes = mano.contendientes()
        if (contendientes.size == 1) return listOf(Bote(mano.pozo, contendientes)).filter { it.cantidad > 0 }
        val niveles = participaciones.map { it.aporteTotal }.filter { it > 0 }.distinct().sorted()
        val botes = mutableListOf<Bote>()
        var anterior = 0L
        for (nivel in niveles) {
            val aportantes = participaciones.filter { it.aporteTotal >= nivel }
            val cantidad = (nivel - anterior) * aportantes.size
            val elegibles = aportantes.map { it.jugadorId }.toSet().intersect(contendientes)
            if (elegibles.isEmpty()) {
                // Dinero muerto: añadirlo al último bote disputable.
                if (botes.isEmpty()) botes.add(Bote(cantidad, contendientes))
                else {
                    val ultimo = botes.removeAt(botes.lastIndex)
                    botes.add(ultimo.copy(cantidad = ultimo.cantidad + cantidad))
                }
            } else botes.add(Bote(cantidad, elegibles))
            anterior = nivel
        }
        return botes
    }

    fun distribuir(mano: EstadoMano, reglas: ReglasVariante): List<PremioBote> {
        val contendientes = mano.contendientes()
        val valores = if (contendientes.size == 1) emptyMap() else
            contendientes.associateWith { id ->
                reglas.evaluar(mano.buscarParticipacion(id)!!.obtenerCartas(), mano.obtenerComunitarias())
            }
        val ordenados = mano.obtenerJugadores().sortedBy { it.asiento.posicion }
        val orden = (ordenados.filter { it.asiento.posicion > mano.boton.posicion } +
            ordenados.filter { it.asiento.posicion <= mano.boton.posicion }).map { it.id }
        val premios = mutableListOf<PremioBote>()
        fun repartir(cantidad: Long, ganadores: Set<JugadorId>) {
            check(ganadores.isNotEmpty())
            val base = cantidad / ganadores.size
            val resto = cantidad % ganadores.size
            orden.filter { it in ganadores }.forEachIndexed { indice, id ->
                val monto = base + if (indice.toLong() < resto) 1L else 0L
                if (monto > 0) premios.add(PremioBote(id, monto))
            }
        }
        for (bote in construir(mano)) {
            if (bote.elegibles.size == 1) {
                repartir(bote.cantidad, bote.elegibles)
                continue
            }
            val altas = bote.elegibles.mapNotNull { id -> valores[id]?.alta?.let { id to it } }
            val bajas = bote.elegibles.mapNotNull { id -> valores[id]?.baja?.let { id to it } }
            val alta = altas.maxOfOrNull { it.second }
            val baja = bajas.maxOfOrNull { it.second }
            val ganadoresAlta = altas.filter { it.second == alta }.map { it.first }.toSet()
            val ganadoresBaja = bajas.filter { it.second == baja }.map { it.first }.toSet()
            when (reglas.modalidad) {
                ModalidadEvaluacion.ALTA -> repartir(bote.cantidad, ganadoresAlta)
                ModalidadEvaluacion.BAJA -> repartir(bote.cantidad, ganadoresBaja)
                ModalidadEvaluacion.ALTA_Y_BAJA -> {
                    if (ganadoresBaja.isEmpty()) repartir(bote.cantidad, ganadoresAlta)
                    else {
                        repartir(bote.cantidad / 2 + bote.cantidad % 2, ganadoresAlta)
                        repartir(bote.cantidad / 2, ganadoresBaja)
                    }
                }
            }
        }
        check(premios.sumOf { it.cantidad } == mano.pozo)
        return premios
    }
}
