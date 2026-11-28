package cr.ac.tec.pokerhorse.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertFailsWith

class AsientoTest {

    @Test
    fun asientoValido() {
        val asiento = Asiento(3)
        assertEquals(3, asiento.posicion)
    }

    @Test
    fun asientoCeroEsValido() {
        assertEquals(0, Asiento(0).posicion)
    }

    @Test
    fun asientoNegativoEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            Asiento(-1)
        }
    }

    @Test
    fun asientosEquivalentes() {
        assertEquals(Asiento(2), Asiento(2))
    }

    @Test
    fun asientosDiferentes() {
        assertNotEquals(Asiento(2), Asiento(3))
    }
}