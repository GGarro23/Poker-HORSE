package cr.ac.tec.pokerhorse.core.variants

import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.evaluation.*

internal enum class ModalidadEvaluacion {
    ALTA,
    BAJA,
    ALTA_Y_BAJA
}

internal data class ResultadoEvaluacion(
    val alta: ValorAlto? = null,
    val baja: ValorBajo? = null
)

internal data class PlanReparto(
    val ocultas: Int = 0,
    val visibles: Int = 0,
    val comunitarias: Int = 0,
    val quemadas: Int = 0
)

internal data class ApuestaForzosa(
    val jugadorId: JugadorId,
    val cantidad: Long,
    val esAnte: Boolean = false
)

internal interface ReglasVariante {

    val variante: Variante

    val calles: List<Calle>

    val modalidad: ModalidadEvaluacion

    fun planReparto(calle: Calle): PlanReparto

    /** Consultar antes de repartir la calle indicada. */
    fun planReparto(calle: Calle, mano: EstadoMano): PlanReparto = planReparto(calle)

    fun apuestaBase(
        calle: Calle,
        configuracion: ConfiguracionPartida
    ): Long

    /** Incrementos completos disponibles al inicio de la calle. */
    fun incrementosPermitidos(
        mano: EstadoMano,
        configuracion: ConfiguracionPartida
    ): Set<Long> = setOf(apuestaBase(mano.calle, configuracion))

    /** Propuesta de proyecto: tres aumentos completos tras la apertura. */
    val maximoAumentosPorCalle: Int get() = 3

    fun cartasVisibles(cartas: List<Carta>): List<Carta>

    fun evaluar(
        propias: List<Carta>,
        comunitarias: List<Carta>
    ): ResultadoEvaluacion

    fun apuestasForzosas(
        mano: EstadoMano,
        configuracion: ConfiguracionPartida
    ): List<ApuestaForzosa>

    fun ordenAccion(mano: EstadoMano): List<JugadorId>
}