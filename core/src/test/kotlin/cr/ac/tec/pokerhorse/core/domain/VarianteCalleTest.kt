package cr.ac.tec.pokerhorse.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class VarianteCalleTest {

    @Test
    fun variantesHorse() {
        assertEquals(
            setOf(
                Variante.HOLD_EM,
                Variante.OMAHA_HI_LO,
                Variante.RAZZ,
                Variante.STUD,
                Variante.STUD_HI_LO
            ),
            Variante.entries.toSet()
        )
    }

    @Test
    fun callesDisponibles() {
        assertEquals(
            setOf(
                Calle.PREFLOP,
                Calle.FLOP,
                Calle.TURN,
                Calle.RIVER,
                Calle.TERCERA,
                Calle.CUARTA,
                Calle.QUINTA,
                Calle.SEXTA,
                Calle.SEPTIMA
            ),
            Calle.entries.toSet()
        )
    }
}