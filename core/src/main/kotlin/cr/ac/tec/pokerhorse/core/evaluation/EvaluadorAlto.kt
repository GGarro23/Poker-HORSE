package cr.ac.tec.pokerhorse.core.evaluation

import cr.ac.tec.pokerhorse.core.domain.Carta

/** Fuerza ascendente; los palos nunca desempatan en showdown. */
internal enum class CategoriaAlta {
    CARTA_ALTA, PAREJA, DOBLE_PAREJA, TRIO, ESCALERA, COLOR,
    FULL_HOUSE, POKER, ESCALERA_COLOR
}

/** Solo contiene la fuerza de la mano, no la identidad ni las cartas elegidas. */
internal class ValorAlto internal constructor(
    val categoria: CategoriaAlta,
    desempates: List<Int>
) : Comparable<ValorAlto> {
    private val valores = desempates.toList()
    val desempates: List<Int> get() = valores.toList()

    override fun compareTo(other: ValorAlto): Int {
        val categoriaComparada = categoria.compareTo(other.categoria)
        if (categoriaComparada != 0) return categoriaComparada
        return compararVectores(valores, other.valores)
    }

    override fun equals(other: Any?): Boolean =
        other is ValorAlto && categoria == other.categoria && valores == other.valores

    override fun hashCode(): Int = 31 * categoria.hashCode() + valores.hashCode()
}

internal fun compararVectores(a: List<Int>, b: List<Int>): Int {
    for (i in 0 until minOf(a.size, b.size)) {
        val comparacion = a[i].compareTo(b[i])
        if (comparacion != 0) return comparacion
    }
    return a.size.compareTo(b.size)
}

internal object EvaluadorAlto {
    fun evaluarCinco(cartas: List<Carta>): ValorAlto {
        require(cartas.size == 5) { "Se requieren exactamente cinco cartas" }
        require(cartas.toSet().size == 5) { "No se permiten cartas duplicadas" }

        val rangos = cartas.map { it.rango.valor }.sortedDescending()
        val grupos = rangos.groupingBy { it }.eachCount().entries.sortedWith(
            compareByDescending<Map.Entry<Int, Int>> { it.value }
                .thenByDescending { it.key }
        )
        val valoresGrupo = grupos.map { it.key }
        val frecuencias = grupos.map { it.value }
        val color = cartas.all { it.palo == cartas.first().palo }
        val mayorEscalera = when {
            rangos == listOf(14, 5, 4, 3, 2) -> 5
            rangos.distinct().size == 5 && rangos.first() - rangos.last() == 4 -> rangos.first()
            else -> null
        }

        return when {
            color && mayorEscalera != null ->
                ValorAlto(CategoriaAlta.ESCALERA_COLOR, listOf(mayorEscalera))
            frecuencias == listOf(4, 1) -> ValorAlto(CategoriaAlta.POKER, valoresGrupo)
            frecuencias == listOf(3, 2) -> ValorAlto(CategoriaAlta.FULL_HOUSE, valoresGrupo)
            color -> ValorAlto(CategoriaAlta.COLOR, rangos)
            mayorEscalera != null -> ValorAlto(CategoriaAlta.ESCALERA, listOf(mayorEscalera))
            frecuencias == listOf(3, 1, 1) -> ValorAlto(CategoriaAlta.TRIO, valoresGrupo)
            frecuencias == listOf(2, 2, 1) -> ValorAlto(CategoriaAlta.DOBLE_PAREJA, valoresGrupo)
            frecuencias == listOf(2, 1, 1, 1) -> ValorAlto(CategoriaAlta.PAREJA, valoresGrupo)
            else -> ValorAlto(CategoriaAlta.CARTA_ALTA, rangos)
        }
    }

    fun mejorDe(cartas: List<Carta>): ValorAlto {
        require(cartas.size in 5..7) { "Se requieren entre cinco y siete cartas" }
        require(cartas.toSet().size == cartas.size) { "No se permiten cartas duplicadas" }
        return combinaciones(cartas, 5).maxOf { evaluarCinco(it) }
    }
}
