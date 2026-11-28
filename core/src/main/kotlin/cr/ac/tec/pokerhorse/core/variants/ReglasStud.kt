package cr.ac.tec.pokerhorse.core.variants

import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.evaluation.compararVectores

internal class ReglasStud(
    private val ordenBajo: Boolean
) {

    val calles: List<Calle>
        get() = listOf(
            Calle.TERCERA,
            Calle.CUARTA,
            Calle.QUINTA,
            Calle.SEXTA,
            Calle.SEPTIMA
        )

    fun planReparto(calle: Calle): PlanReparto =
        when (calle) {
            Calle.TERCERA ->
                PlanReparto(ocultas = 2, visibles = 1)

            Calle.CUARTA, Calle.QUINTA, Calle.SEXTA ->
                PlanReparto(visibles = 1, quemadas = 1)

            Calle.SEPTIMA ->
                PlanReparto(ocultas = 1, quemadas = 1)

            else -> error("Calle no válida")
        }

    fun apuestaBase(
        calle: Calle,
        config: ConfiguracionPartida
    ): Long = when (calle) {
        Calle.TERCERA, Calle.CUARTA ->
            config.apuestaPequena

        Calle.QUINTA, Calle.SEXTA, Calle.SEPTIMA ->
            config.apuestaGrande

        else -> error("Calle no válida")
    }

    fun cartasVisibles(cartas: List<Carta>): List<Carta> =
        cartas.drop(2).take(4)

    private fun valor(carta: Carta): Int =
        if (ordenBajo && carta.rango == Rango.AS)
            1
        else
            carta.rango.valor

    private fun valorPalo(palo: Palo): Int = when (palo) {
        Palo.TREBOL -> 0
        Palo.DIAMANTE -> 1
        Palo.CORAZON -> 2
        Palo.PICA -> 3
    }

    private fun visible(
        mano: EstadoMano,
        jugador: Jugador
    ): List<Carta> =
        cartasVisibles(
            mano.buscarParticipacion(jugador.id)!!
                .obtenerCartas()
        )

    private fun bringIn(mano: EstadoMano): Jugador? {

        val candidatos = participantes(mano)
            .filter {
                mano.buscarParticipacion(it.id)?.estado !=
                        EstadoJugador.RETIRADO
            }

        val comparador = compareBy<Jugador> {
            valor(visible(mano, it).first())
        }.thenBy {
            valorPalo(visible(mano, it).first().palo)
        }

        return if (ordenBajo)
            candidatos.maxWithOrNull(comparador)
        else
            candidatos.minWithOrNull(comparador)
    }

    fun apuestasForzosas(
        mano: EstadoMano,
        config: ConfiguracionPartida
    ): List<ApuestaForzosa> {

        require(mano.calle == Calle.TERCERA)
        val antes = puedenActuar(mano)
            .filter { config.ante > 0L }
            .map { ApuestaForzosa(it.id, config.ante, true) }

        val original = bringIn(mano) ?: return antes
        // Se calcula antes de contabilizar los antes.
        val candidatos = puedenActuar(mano)
            .filter { it.fichas > config.ante }
        val obligado = rotarDesde(candidatos, original.asiento, true)
            .firstOrNull() ?: return antes
        return antes + ApuestaForzosa(obligado.id, config.bringIn)
    }

    private fun fuerzaVisible(
        cartas: List<Carta>
    ): Pair<List<Int>, List<Int>> {

        val grupos = cartas.map { valor(it) }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedWith(
                compareByDescending<Map.Entry<Int, Int>> {
                    it.value
                }.thenByDescending {
                    it.key
                }
            )

        return grupos.map { it.value } to
                grupos.map { it.key }
    }

    private fun compararVisibles(
        a: List<Carta>,
        b: List<Carta>
    ): Int {

        val (frecuenciasA, rangosA) = fuerzaVisible(a)
        val (frecuenciasB, rangosB) = fuerzaVisible(b)

        val frecuencias = compararVectores(
            frecuenciasA,
            frecuenciasB
        )

        val resultado = if (frecuencias != 0)
            frecuencias
        else
            compararVectores(rangosA, rangosB)

        return if (ordenBajo)
            -resultado
        else
            resultado
    }

    fun ordenAccion(mano: EstadoMano): List<JugadorId> {

        val candidatos = rotarDesde(
            puedenActuar(mano),
            mano.boton
        )

        if (candidatos.isEmpty()) return emptyList()

        if (mano.calle == Calle.TERCERA) {

            val obligado = bringIn(mano)
                ?: return emptyList()

            return rotarDesde(
                candidatos,
                obligado.asiento,
                incluirAsiento = true
            ).map { it.id }
        }

        val primero = candidatos.maxWithOrNull(
            Comparator { a, b ->
                compararVisibles(
                    visible(mano, a),
                    visible(mano, b)
                )
            }
        ) ?: return emptyList()

        return rotarDesde(
            candidatos,
            primero.asiento,
            incluirAsiento = true
        ).map { it.id }
    }
}