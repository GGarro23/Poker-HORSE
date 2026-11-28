package cr.ac.tec.pokerhorse.core.variants

import cr.ac.tec.pokerhorse.core.domain.Variante

internal object Variantes {

    private val estrategias = listOf(
        TexasHoldem,
        OmahaHiLo,
        Razz,
        SevenCardStud,
        StudEightOrBetter
    ).associateBy { it.variante }

    fun obtener(variante: Variante): ReglasVariante =
        estrategias.getValue(variante)
}