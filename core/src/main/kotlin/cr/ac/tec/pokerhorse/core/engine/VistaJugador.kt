package cr.ac.tec.pokerhorse.core.engine

import cr.ac.tec.pokerhorse.core.domain.*

data class VistaParticipante(
    val id: JugadorId,
    val nombre: String,
    val asiento: Asiento,
    val fichas: Long,
    val aporteCalle: Long,
    val aporteTotal: Long,
    val estado: EstadoJugador?,
    val cartasVisibles: List<Carta>,
    val cantidadCartasOcultas: Int = 0
)

/** Proyección sin estado autoritativo, cartas privadas ajenas ni mazo. */
data class VistaJugador(
    val jugadorId: JugadorId,
    val variante: Variante,
    val calle: Calle,
    val boton: Asiento,
    val turno: JugadorId?,
    val pozo: Long,
    val participantes: List<VistaParticipante>,
    val propias: List<Carta>,
    val comunitarias: List<Carta>,
    val accionesLegales: List<Accion>,
    val finalizada: Boolean,
    val premios: List<PremioBote>,
    val manosCompletadas: Long,
    val montosAcciones: Map<Accion, Long> = emptyMap()
)
