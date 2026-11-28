package cr.ac.tec.pokerhorse.core.engine

import cr.ac.tec.pokerhorse.core.domain.*

/** Estado autoritativo. No debe enviarse directamente a clientes LAN. */
class EstadoMotor internal constructor(
    val partida: EstadoPartida,
    internal val ronda: RondaApuestas?,
    val resultado: ResultadoMano? = null
) {
    val mano: EstadoMano get() = partida.manoActual
    val finalizada: Boolean get() = resultado != null
}

internal data class RondaApuestas(
    val orden: List<JugadorId>,
    val pendientes: Set<JugadorId>,
    val derechoSubida: Set<JugadorId>,
    val objetivo: Long,
    val incrementos: Set<Long>,
    val abierta: Boolean,
    val maximoAumentos: Int
)

data class PremioBote(val jugadorId: JugadorId, val cantidad: Long)

class ResultadoMano internal constructor(premios: List<PremioBote>) {
    private val premiosInternos = premios.toList()
    val premios: List<PremioBote> get() = premiosInternos.toList()
}

internal fun EstadoMano.reconstruir(
    calle: Calle = this.calle,
    mazo: Mazo = this.mazo,
    jugadores: List<Jugador> = obtenerJugadores(),
    participaciones: List<ParticipacionMano> = obtenerParticipaciones(),
    comunitarias: List<Carta> = obtenerComunitarias(),
    turno: JugadorId? = this.turno,
    aumentos: Int = aumentosCalle
): EstadoMano = EstadoMano(variante, calle, mazo, jugadores, participaciones,
    comunitarias, turno, boton, aumentos)

internal fun EstadoMano.activos(): Set<JugadorId> = obtenerParticipaciones()
    .filter { it.estado == EstadoJugador.ACTIVO && buscarJugador(it.jugadorId)!!.fichas > 0 }
    .map { it.jugadorId }.toSet()

internal fun EstadoMano.contendientes(): Set<JugadorId> = obtenerParticipaciones()
    .filter { it.estado != EstadoJugador.RETIRADO }.map { it.jugadorId }.toSet()
