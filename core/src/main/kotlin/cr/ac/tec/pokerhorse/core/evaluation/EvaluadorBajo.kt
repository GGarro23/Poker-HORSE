package cr.ac.tec.pokerhorse.core.evaluation

import cr.ac.tec.pokerhorse.core.domain.Carta

internal enum class CategoriaBaja {
    POKER,
    FULL_HOUSE,
    TRIO,
    DOBLE_PAREJA,
    PAREJA,
    SIN_PAREJA
}

internal class ValorBajo internal constructor(
    val categoria: CategoriaBaja,
    desempates: List<Int>
) : Comparable<ValorBajo> {

    private val valores = desempates.toList()

    val desempates: List<Int>
        get() = valores.toList()

    val calificaOchoOMejor: Boolean
        get() = categoria == CategoriaBaja.SIN_PAREJA &&
                valores.first() <= 8

    override fun compareTo(other: ValorBajo): Int {
        val comparacion =
            categoria.compareTo(other.categoria)

        if (comparacion != 0) return comparacion

        return compararVectores(
            other.valores,
            valores
        )
    }

    override fun equals(other: Any?): Boolean =
        other is ValorBajo &&
                categoria == other.categoria &&
                valores == other.valores

    override fun hashCode(): Int =
        31 * categoria.hashCode() + valores.hashCode()
}

internal object EvaluadorBajo {

    fun evaluarCinco(cartas: List<Carta>): ValorBajo {
        require(cartas.size == 5) {
            "Se requieren exactamente cinco cartas"
        }

        require(cartas.toSet().size == 5) {
            "No se permiten cartas duplicadas"
        }

        val rangos = cartas.map {
            if (it.rango.valor == 14) 1
            else it.rango.valor
        }

        val grupos = rangos
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

        val frecuencias = grupos.map { it.value }
        val valores = grupos.map { it.key }

        val categoria = when (frecuencias) {
            listOf(4, 1) ->
                CategoriaBaja.POKER

            listOf(3, 2) ->
                CategoriaBaja.FULL_HOUSE

            listOf(3, 1, 1) ->
                CategoriaBaja.TRIO

            listOf(2, 2, 1) ->
                CategoriaBaja.DOBLE_PAREJA

            listOf(2, 1, 1, 1) ->
                CategoriaBaja.PAREJA

            else ->
                CategoriaBaja.SIN_PAREJA
        }

        return ValorBajo(categoria, valores)
    }

    fun mejorDe(
        cartas: List<Carta>,
        ochoOMejor: Boolean = false
    ): ValorBajo? {

        require(cartas.size in 5..7) {
            "Se requieren entre cinco y siete cartas"
        }

        require(cartas.toSet().size == cartas.size) {
            "No se permiten cartas duplicadas"
        }

        return combinaciones(cartas, 5)
            .map { evaluarCinco(it) }
            .filter {
                !ochoOMejor || it.calificaOchoOMejor
            }
            .maxOrNull()
    }
}