package cr.ac.tec.pokerhorse.core.evaluation

/** Genera subconjuntos por índices crecientes, sin repetir permutaciones. */
internal fun <T> combinaciones(elementos: List<T>, cantidad: Int): Sequence<List<T>> {
    require(cantidad in 0..elementos.size)
    val entrada = elementos.toList()
    return sequence {
        val elegidos = mutableListOf<T>()
        suspend fun SequenceScope<List<T>>.visitar(inicio: Int) {
            if (elegidos.size == cantidad) {
                yield(elegidos.toList())
                return
            }
            val faltantes = cantidad - elegidos.size
            for (i in inicio..entrada.size - faltantes) {
                elegidos.add(entrada[i])
                visitar(i + 1)
                elegidos.removeAt(elegidos.lastIndex)
            }
        }
        visitar(0)
    }
}
