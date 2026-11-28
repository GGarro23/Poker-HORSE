package cr.ac.tec.pokerhorse.core.evaluation

import cr.ac.tec.pokerhorse.core.domain.Carta
import cr.ac.tec.pokerhorse.core.domain.Palo
import cr.ac.tec.pokerhorse.core.domain.Rango
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class EvaluadorAltoTest {
    private fun cartas(texto: String): List<Carta> = texto.split(" ").map {
        val rango = Rango.entries["23456789TJQKA".indexOf(it[0])]
        val palo = when (it[1]) {
            's' -> Palo.PICA
            'd' -> Palo.DIAMANTE
            'c' -> Palo.TREBOL
            'h' -> Palo.CORAZON
            else -> error("Palo desconocido")
        }
        Carta(palo, rango)
    }

    private fun evaluar(texto: String) = EvaluadorAlto.evaluarCinco(cartas(texto))

    @Test fun categoriasYOrden() {
        val ejemplos = listOf(
            "As Kd 9c 7h 2s", "As Ad Kc 7h 2s", "As Ad Kc Kh 2s",
            "As Ad Ac Kh 2s", "2s 3d 4c 5h 6s", "As Ks 9s 7s 2s",
            "As Ad Ac Kh Ks", "As Ad Ac Ah 2s", "Ts Js Qs Ks As"
        )
        val resultados = ejemplos.map(::evaluar)
        assertEquals(CategoriaAlta.entries.toList(), resultados.map { it.categoria })
        resultados.zipWithNext().forEach { (a, b) -> assertTrue(a < b) }
    }

    @Test fun desempatesPorCategoria() {
        val pares = listOf(
            "As Kd 9c 7h 3s" to "As Kd 9c 7h 2s",
            "As Ad Kc 7h 3s" to "As Ad Kc 7h 2s",
            "As Ad Qc 7h 3s" to "Ks Kd Qc Jh 9s",
            "As Ad Kc Kh 3s" to "As Ad Kc Kh 2s",
            "As Ad Kc Kh 2s" to "As Ad Qc Qh Ks",
            "As Ad Ac Kh 3s" to "As Ad Ac Kh 2s",
            "3s 4d 5c 6h 7s" to "2s 3d 4c 5h 6s",
            "As Ks 9s 7s 3s" to "Ah Kh 9h 7h 2h",
            "As Ad Ac Kh Ks" to "As Ad Ac Qh Qs",
            "Ks Kd Kc 2h 2s" to "Qs Qd Qc Ah As",
            "As Ad Ac Ah Ks" to "As Ad Ac Ah Qs",
            "Ts Js Qs Ks As" to "9h Th Jh Qh Kh"
        )
        pares.forEach { (ganadora, perdedora) ->
            assertTrue(evaluar(ganadora) > evaluar(perdedora), ganadora)
            assertTrue(evaluar(perdedora) < evaluar(ganadora), perdedora)
        }
    }

    @Test fun escaleraAsBajoYFalsasEscaleras() {
        val wheel = evaluar("As 2d 3c 4h 5s")
        assertEquals(CategoriaAlta.ESCALERA, wheel.categoria)
        assertEquals(listOf(5), wheel.desempates)
        assertTrue(wheel < evaluar("2s 3d 4c 5h 6s"))
        assertEquals(listOf(5), evaluar("As 2s 3s 4s 5s").desempates)
        assertEquals(CategoriaAlta.CARTA_ALTA, evaluar("Qs Kd Ac 2h 3s").categoria)
        assertEquals(CategoriaAlta.PAREJA, evaluar("2s 2d 3c 4h 6s").categoria)
    }

    @Test fun mejorEntreSiete() {
        assertEquals(evaluar("As Ad Ac Kh Ks"),
            EvaluadorAlto.mejorDe(cartas("As Ad Ac Kh Ks Kd 2s")))
        assertEquals(evaluar("Ts Js Qs Ks As"),
            EvaluadorAlto.mejorDe(cartas("Ts Js Qs Ks As 2d 3c")))
        assertEquals(21, combinaciones((1..7).toList(), 5).count())
        assertEquals(21, combinaciones((1..7).toList(), 5).toSet().size)
        assertEquals(6, combinaciones((1..6).toList(), 5).count())
    }

    @Test fun igualdadDeterminismoYNoMutacion() {
        val a = evaluar("As Kd 9c 7h 2s")
        val b = evaluar("Ah Ks 9d 7c 2h")
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals(0, a.compareTo(b))
        val entrada = cartas("As Ad Ac Kh Ks Kd 2s").toMutableList()
        val copia = entrada.toList()
        val mejor = EvaluadorAlto.mejorDe(entrada)
        assertEquals(mejor, EvaluadorAlto.mejorDe(entrada.reversed()))
        assertEquals(copia, entrada)
        val vector = mejor.desempates.toMutableList()
        vector.clear()
        assertEquals(listOf(14, 13), mejor.desempates)
    }

    @Test fun entradasInvalidas() {
        assertFailsWith<IllegalArgumentException> { evaluar("As As 9c 7h 2s") }
        assertFailsWith<IllegalArgumentException> { evaluar("As Kd 9c 7h") }
        assertFailsWith<IllegalArgumentException> { EvaluadorAlto.mejorDe(cartas("As Ad Ac Kh")) }
        assertFailsWith<IllegalArgumentException> {
            EvaluadorAlto.mejorDe(cartas("As Ad Ac Kh Ks Kd As"))
        }
    }
}
