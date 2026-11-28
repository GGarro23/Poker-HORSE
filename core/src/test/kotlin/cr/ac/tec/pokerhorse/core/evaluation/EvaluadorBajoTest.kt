package cr.ac.tec.pokerhorse.core.evaluation

import cr.ac.tec.pokerhorse.core.domain.Carta
import cr.ac.tec.pokerhorse.core.domain.Palo
import cr.ac.tec.pokerhorse.core.domain.Rango
import kotlin.test.*

class EvaluadorBajoTest {

    private fun cartas(texto: String): List<Carta> =
        texto.split(" ").map {
            val rango =
                Rango.entries["23456789TJQKA".indexOf(it[0])]

            val palo = when (it[1]) {
                's' -> Palo.PICA
                'd' -> Palo.DIAMANTE
                'c' -> Palo.TREBOL
                'h' -> Palo.CORAZON
                else -> error("Palo desconocido")
            }

            Carta(palo, rango)
        }

    private fun evaluar(texto: String) =
        EvaluadorBajo.evaluarCinco(cartas(texto))

    @Test
    fun mejorManoPosible() {
        val resultado = evaluar("As 2d 3c 4h 5s")

        assertEquals(
            CategoriaBaja.SIN_PAREJA,
            resultado.categoria
        )

        assertEquals(
            listOf(5, 4, 3, 2, 1),
            resultado.desempates
        )

        assertTrue(resultado.calificaOchoOMejor)
    }

    @Test
    fun ignoraEscalerasYColores() {
        assertEquals(
            evaluar("As 2s 3s 4s 5s"),
            evaluar("Ah 2d 3c 4h 5d")
        )
    }

    @Test
    fun desempateLexicografico() {
        assertTrue(
            evaluar("7s 5d 4c 2h As") >
                    evaluar("7h 6d 3c 2s Ah")
        )

        assertTrue(
            evaluar("6s 5d 4c 3h As") >
                    evaluar("7s 4d 3c 2h As")
        )
    }

    @Test
    fun calificadorOchoOMejor() {
        assertTrue(
            evaluar("8s 7d 4c 2h As").calificaOchoOMejor
        )

        assertFalse(
            evaluar("9s 7d 4c 2h As").calificaOchoOMejor
        )

        assertFalse(
            evaluar("8s 7d 4c Ah As").calificaOchoOMejor
        )
    }

    @Test
    fun categoriasBajas() {
        val manos = listOf(
            "As Ad Ac Ah 2s",
            "As Ad Ac 2h 2s",
            "As Ad Ac 2h 3s",
            "As Ad 2c 2h 3s",
            "As Ad 2c 3h 4s",
            "9s Td Jc Qh Ks"
        )

        val resultados = manos.map(::evaluar)

        assertEquals(
            CategoriaBaja.entries.toList(),
            resultados.map { it.categoria }
        )

        resultados.zipWithNext().forEach { (a, b) ->
            assertTrue(a < b)
        }
    }

    @Test
    fun razzConParejas() {
        assertTrue(
            evaluar("As Ad 7c 6h 5s") >
                    evaluar("2s 2d 5c 4h 3s")
        )

        assertTrue(
            evaluar("As Ad 7c 5h 4s") >
                    evaluar("As Ad 7c 6h 4s")
        )
    }

    @Test
    fun mejorEntreSiete() {
        val entrada = cartas("As 2d 3c 4h 5s Kd Qc")

        assertEquals(
            evaluar("As 2d 3c 4h 5s"),
            EvaluadorBajo.mejorDe(entrada)
        )

        assertEquals(
            EvaluadorBajo.mejorDe(entrada),
            EvaluadorBajo.mejorDe(entrada.reversed())
        )
    }

    @Test
    fun ausenciaDeLowCalificado() {
        val entrada = cartas("As 2d 3c 4h 9s Kd Qc")

        assertNull(
            EvaluadorBajo.mejorDe(
                entrada,
                ochoOMejor = true
            )
        )

        assertNotNull(
            EvaluadorBajo.mejorDe(entrada)
        )
    }

    @Test
    fun empates() {
        val a = evaluar("As 2d 3c 4h 5s")
        val b = evaluar("Ah 2s 3d 4c 5h")

        assertEquals(a, b)
        assertEquals(0, a.compareTo(b))
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun entradasInvalidas() {
        assertFailsWith<IllegalArgumentException> {
            evaluar("As As 3c 4h 5s")
        }

        assertFailsWith<IllegalArgumentException> {
            evaluar("As 2d 3c 4h")
        }

        assertFailsWith<IllegalArgumentException> {
            EvaluadorBajo.mejorDe(
                cartas("As 2d 3c 4h 5s Kd As")
            )
        }
    }
}