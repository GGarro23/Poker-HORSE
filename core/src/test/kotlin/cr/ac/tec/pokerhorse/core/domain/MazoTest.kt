package cr.ac.tec.pokerhorse.core.domain

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class MazoTest {
    @Test
    fun mazoTiene52Cartas() {
        val mazo = Mazo.crear()

        assertEquals(52, mazo.cantidad)
    }

    @Test
    fun mazoNuevoNoEstaVacio() {
        val mazo = Mazo.crear()

        assertFalse(mazo.estaVacio())
    }

    @Test
    fun extraccionNoModificaOriginal() {
        val mazoOriginal = Mazo.crear()
        val resultado = mazoOriginal.extraer()

        assertEquals(52, mazoOriginal.cantidad)
        assertEquals(51, resultado.mazoRestante.cantidad)
    }

    @Test
    fun extraerTodasLasCartas() {
        var mazo = Mazo.crear()
        val extraidas = mutableListOf<Carta>()

        while (!mazo.estaVacio()) {
            val extraida = mazo.extraer()
            extraidas.add(extraida.carta)
            mazo = extraida.mazoRestante
        }

        assertEquals(52, extraidas.size)
        assertEquals(52, extraidas.toSet().size)
        assertEquals(0, mazo.cantidad)
        assertTrue(mazo.estaVacio())
    }

    @Test
    fun extraerDeMazoVacio() {
        var mazo = Mazo.crear()

        repeat(52) {
            mazo = mazo.extraer().mazoRestante
        }

        assertFailsWith<IllegalStateException> {
            mazo.extraer()
        }
    }

    private fun obtenerCartas(mazo: Mazo): List<Carta> {
        var actual = mazo
        val cartas = mutableListOf<Carta>()

        while (!actual.estaVacio()) {
            val (carta, restante) = actual.extraer()
            cartas.add(carta)
            actual = restante
        }

        return cartas
    }

    @Test
    fun mezclaReproducible() {
        val mazo = Mazo.crear()

        val mezcla1 = mazo.mezclar(Random(42))
        val mezcla2 = mazo.mezclar(Random(42))

        assertEquals(
            obtenerCartas(mezcla1),
            obtenerCartas(mezcla2)
        )
    }

    @Test
    fun mezclaConservaCartas() {
        val mazo = Mazo.crear()
        val mezclado = mazo.mezclar(Random(42))

        assertEquals(52, mezclado.cantidad)

        assertEquals(
            obtenerCartas(mazo).toSet(),
            obtenerCartas(mezclado).toSet()
        )
    }

    @Test
    fun mezclaNoModificaOriginal() {
        val mazo = Mazo.crear()
        val ordenOriginal = obtenerCartas(mazo)

        mazo.mezclar(Random(42))

        assertEquals(ordenOriginal, obtenerCartas(mazo))
    }

    @Test
    fun mezclaCambiaOrden() {
        val mazo = Mazo.crear()
        val mezclado = mazo.mezclar(Random(42))

        assertNotEquals(
            obtenerCartas(mazo),
            obtenerCartas(mezclado)
        )
    }

}