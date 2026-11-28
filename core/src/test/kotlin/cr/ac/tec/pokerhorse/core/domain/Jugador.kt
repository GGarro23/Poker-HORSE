package cr.ac.tec.pokerhorse.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertFailsWith

class JugadorIdTest {

    @Test
    fun identificadorValido() {
        val id = JugadorId(15)
        assertEquals(15, id.identificador)
    }

    @Test
    fun identificadorCeroEsValido() {
        assertEquals(0, JugadorId(0).identificador)
    }

    @Test
    fun identificadorNegativoEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            JugadorId(-1)
        }
    }

    @Test
    fun identificadoresEquivalentes() {
        assertEquals(JugadorId(5), JugadorId(5))
    }

    @Test
    fun identificadoresDiferentes() {
        assertNotEquals(JugadorId(5), JugadorId(6))
    }
}