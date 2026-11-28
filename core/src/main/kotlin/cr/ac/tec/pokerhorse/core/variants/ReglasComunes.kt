package cr.ac.tec.pokerhorse.core.variants

import cr.ac.tec.pokerhorse.core.domain.*

internal fun participantes(mano: EstadoMano): List<Jugador> =
    mano.obtenerJugadores()
        .filter { mano.buscarParticipacion(it.id) != null }
        .sortedBy { it.asiento.posicion }

internal fun puedenActuar(mano: EstadoMano): List<Jugador> =
    participantes(mano).filter {
        mano.buscarParticipacion(it.id)?.estado ==
                EstadoJugador.ACTIVO && it.fichas > 0
    }

internal fun rotarDesde(
    jugadores: List<Jugador>,
    asiento: Asiento,
    incluirAsiento: Boolean = false
): List<Jugador> {

    val ordenados = jugadores.sortedBy {
        it.asiento.posicion
    }

    val indice = ordenados.indexOfFirst {
        if (incluirAsiento)
            it.asiento.posicion >= asiento.posicion
        else
            it.asiento.posicion > asiento.posicion
    }

    if (indice == -1) return ordenados

    return ordenados.drop(indice) +
            ordenados.take(indice)
}