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

    fun apuestaBase(
        calle: Calle,
        configuracion: ConfiguracionPartida
    ): Long

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