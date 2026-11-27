package cr.ac.tec.pokerhorse.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertFailsWith

class AccionTest {

    @Test
    fun accionesSinMonto() {
        val acciones: Set<Accion> = setOf(
            Accion.Fold,
            Accion.Check,
            Accion.Call,
            Accion.AllIn
        )

        assertEquals(4, acciones.size)
    }

    @Test
    fun apuestaValida() {
        val accion = Accion.Bet(200L)

        assertEquals(200L, accion.cantidad)
    }

    @Test
    fun apuestaCeroEsInvalida() {
        assertFailsWith<IllegalArgumentException> {
            Accion.Bet(0L)
        }
    }

    @Test
    fun apuestaNegativaEsInvalida() {
        assertFailsWith<IllegalArgumentException> {
            Accion.Bet(-100L)
        }
    }

    @Test
    fun aumentoValido() {
        val accion = Accion.Raise(500L)

        assertEquals(500L, accion.hasta)
    }

    @Test
    fun aumentoCeroEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            Accion.Raise(0L)
        }
    }

    @Test
    fun aumentoNegativoEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            Accion.Raise(-100L)
        }
    }

    @Test
    fun apuestasEquivalentes() {
        assertEquals(
            Accion.Bet(100L),
            Accion.Bet(100L)
        )
    }

    @Test
    fun apuestasDiferentes() {
        assertNotEquals(
            Accion.Bet(100L),
            Accion.Bet(200L)
        )
    }

    @Test
    fun aumentosEquivalentes() {
        assertEquals(
            Accion.Raise(300L),
            Accion.Raise(300L)
        )
    }

    @Test
    fun aumentosDiferentes() {
        assertNotEquals(
            Accion.Raise(300L),
            Accion.Raise(500L)
        )
    }

    @Test
    fun apuestaYAumentoSonAccionesDiferentes() {
        val apuesta: Accion = Accion.Bet(300L)
        val aumento: Accion = Accion.Raise(300L)

        assertNotEquals(apuesta, aumento)
    }
}