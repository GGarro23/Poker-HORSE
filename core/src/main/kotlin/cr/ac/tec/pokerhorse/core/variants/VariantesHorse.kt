package cr.ac.tec.pokerhorse.core.variants

import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.evaluation.*

internal class EstrategiaComunitaria(
    override val variante: Variante,
    override val modalidad: ModalidadEvaluacion,
    cantidadPrivadas: Int,
    private val evaluacion:
        (List<Carta>, List<Carta>) -> ResultadoEvaluacion
) : ReglasVariante {

    private val reglas =
        ReglasComunitarias(cantidadPrivadas)

    override val calles
        get() = reglas.calles

    override fun planReparto(calle: Calle) =
        reglas.planReparto(calle)

    override fun apuestaBase(
        calle: Calle,
        configuracion: ConfiguracionPartida
    ) = reglas.apuestaBase(calle, configuracion)

    override fun cartasVisibles(
        cartas: List<Carta>
    ): List<Carta> = emptyList()

    override fun evaluar(
        propias: List<Carta>,
        comunitarias: List<Carta>
    ) = evaluacion(propias, comunitarias)

    override fun apuestasForzosas(
        mano: EstadoMano,
        configuracion: ConfiguracionPartida
    ) = reglas.apuestasForzosas(mano, configuracion)

    override fun ordenAccion(mano: EstadoMano) =
        reglas.ordenAccion(mano)
}

internal class EstrategiaStud(
    override val variante: Variante,
    override val modalidad: ModalidadEvaluacion,
    ordenBajo: Boolean,
    private val evaluacion:
        (List<Carta>) -> ResultadoEvaluacion
) : ReglasVariante {

    private val reglas = ReglasStud(ordenBajo)

    override val calles
        get() = reglas.calles

    override fun planReparto(calle: Calle) =
        reglas.planReparto(calle)

    override fun planReparto(calle: Calle, mano: EstadoMano): PlanReparto {
        val normal = reglas.planReparto(calle)
        if (calle != Calle.SEPTIMA) return normal
        val contendientes = mano.obtenerParticipaciones()
            .count { it.estado != EstadoJugador.RETIRADO }
        if (mano.mazo.cantidad >= contendientes + normal.quemadas) return normal
        check(mano.mazo.cantidad >= 1) { "No hay cartas para completar séptima calle" }
        return PlanReparto(
            comunitarias = 1,
            quemadas = if (mano.mazo.cantidad >= 2) 1 else 0
        )
    }

    override fun apuestaBase(
        calle: Calle,
        configuracion: ConfiguracionPartida
    ) = reglas.apuestaBase(calle, configuracion)

    override fun cartasVisibles(cartas: List<Carta>) =
        reglas.cartasVisibles(cartas)

    override fun evaluar(
        propias: List<Carta>,
        comunitarias: List<Carta>
    ): ResultadoEvaluacion {
        require(
            (propias.size == 7 && comunitarias.isEmpty()) ||
                (propias.size == 6 && comunitarias.size == 1)
        ) { "Cantidad de cartas inválida para Stud" }
        return evaluacion(propias + comunitarias)
    }

    override fun apuestasForzosas(
        mano: EstadoMano,
        configuracion: ConfiguracionPartida
    ) = reglas.apuestasForzosas(mano, configuracion)

    override fun ordenAccion(mano: EstadoMano) =
        reglas.ordenAccion(mano)
}

internal object TexasHoldem : ReglasVariante by
EstrategiaComunitaria(
    Variante.HOLD_EM,
    ModalidadEvaluacion.ALTA,
    2,
    { propias, comunitarias ->

        require(propias.size == 2)
        require(comunitarias.size == 5)

        ResultadoEvaluacion(
            alta = EvaluadorAlto.mejorDe(
                propias + comunitarias
            )
        )
    }
)

internal object OmahaHiLo : ReglasVariante by
EstrategiaComunitaria(
    Variante.OMAHA_HI_LO,
    ModalidadEvaluacion.ALTA_Y_BAJA,
    4,
    { propias, comunitarias ->

        require(propias.size == 4)
        require(comunitarias.size == 5)
        require(
            (propias + comunitarias).toSet().size == 9
        )

        val combinacionesValidas =
            combinaciones(propias, 2).flatMap { dos ->
                combinaciones(comunitarias, 3).map { tres ->
                    dos + tres
                }
            }.toList()

        val alta = combinacionesValidas.maxOf {
            EvaluadorAlto.evaluarCinco(it)
        }

        val baja = combinacionesValidas
            .map { EvaluadorBajo.evaluarCinco(it) }
            .filter { it.calificaOchoOMejor }
            .maxOrNull()

        ResultadoEvaluacion(alta, baja)
    }
)

internal object Razz : ReglasVariante by
EstrategiaStud(
    Variante.RAZZ,
    ModalidadEvaluacion.BAJA,
    true,
    { propias ->
        ResultadoEvaluacion(
            baja = EvaluadorBajo.mejorDe(propias)
        )
    }
)

internal object SevenCardStud : ReglasVariante by
EstrategiaStud(
    Variante.STUD,
    ModalidadEvaluacion.ALTA,
    false,
    { propias ->
        ResultadoEvaluacion(
            alta = EvaluadorAlto.mejorDe(propias)
        )
    }
) {
    override fun incrementosPermitidos(
        mano: EstadoMano,
        configuracion: ConfiguracionPartida
    ): Set<Long> {
        val base = apuestaBase(mano.calle, configuracion)
        if (mano.calle != Calle.CUARTA) return setOf(base)
        val parejaVisible = mano.obtenerParticipaciones().any { p ->
            val visibles = cartasVisibles(p.obtenerCartas())
            p.estado != EstadoJugador.RETIRADO && visibles.size == 2 &&
                visibles[0].rango == visibles[1].rango
        }
        return if (parejaVisible) setOf(base, configuracion.apuestaGrande)
        else setOf(base)
    }
}

internal object StudEightOrBetter : ReglasVariante by
EstrategiaStud(
    Variante.STUD_HI_LO,
    ModalidadEvaluacion.ALTA_Y_BAJA,
    false,
    { propias ->
        ResultadoEvaluacion(
            alta = EvaluadorAlto.mejorDe(propias),
            baja = EvaluadorBajo.mejorDe(
                propias,
                ochoOMejor = true
            )
        )
    }
)