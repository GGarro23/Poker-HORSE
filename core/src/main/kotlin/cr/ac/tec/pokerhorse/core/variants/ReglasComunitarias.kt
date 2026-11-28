package cr.ac.tec.pokerhorse.core.variants

import cr.ac.tec.pokerhorse.core.domain.*

internal class ReglasComunitarias(
    private val cantidadPrivadas: Int
) {

    val calles: List<Calle>
        get() = listOf(
            Calle.PREFLOP,
            Calle.FLOP,
            Calle.TURN,
            Calle.RIVER
        )

    fun planReparto(calle: Calle): PlanReparto =
        when (calle) {
            Calle.PREFLOP ->
                PlanReparto(ocultas = cantidadPrivadas)

            Calle.FLOP ->
                PlanReparto(comunitarias = 3, quemadas = 1)

            Calle.TURN, Calle.RIVER ->
                PlanReparto(comunitarias = 1, quemadas = 1)

            else -> error("Calle no válida")
        }

    fun apuestaBase(
        calle: Calle,
        config: ConfiguracionPartida
    ): Long = when (calle) {
        Calle.PREFLOP, Calle.FLOP ->
            config.apuestaPequena

        Calle.TURN, Calle.RIVER ->
            config.apuestaGrande

        else -> error("Calle no válida")
    }

    private fun ciegas(
        mano: EstadoMano
    ): Pair<Jugador, Jugador> {

        val jugadores = participantes(mano)
        require(jugadores.size >= 2)

        val ordenados = rotarDesde(
            jugadores,
            mano.boton,
            incluirAsiento = jugadores.size == 2
        )

        return ordenados[0] to ordenados[1]
    }

    fun apuestasForzosas(
        mano: EstadoMano,
        config: ConfiguracionPartida
    ): List<ApuestaForzosa> {

        val (pequena, grande) = ciegas(mano)

        return listOf(
            ApuestaForzosa(
                pequena.id,
                maxOf(1L, config.apuestaPequena / 2)
            ),
            ApuestaForzosa(
                grande.id,
                config.apuestaPequena
            )
        )
    }

    fun ordenAccion(
        mano: EstadoMano
    ): List<JugadorId> {

        val referencia = if (mano.calle == Calle.PREFLOP)
            ciegas(mano).second.asiento
        else
            mano.boton

        return rotarDesde(
            puedenActuar(mano),
            referencia
        ).map { it.id }
    }
}