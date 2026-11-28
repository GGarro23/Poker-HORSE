package cr.ac.tec.pokerhorse.core.variants

import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.evaluation.*
import kotlin.test.*

class VariantesHorseTest {

    private fun cartas(texto: String): List<Carta> =
        texto.split(" ").map {
            Carta(
                when (it[1]) {
                    's' -> Palo.PICA
                    'd' -> Palo.DIAMANTE
                    'c' -> Palo.TREBOL
                    'h' -> Palo.CORAZON
                    else -> error("Palo desconocido")
                },
                Rango.entries[
                    "23456789TJQKA".indexOf(it[0])
                ]
            )
        }

    @Test
    fun cincoEstrategiasPolimorficas() {
        val estrategias: List<ReglasVariante> =
            Variante.entries.map {
                Variantes.obtener(it)
            }

        assertEquals(5, estrategias.size)

        assertEquals(
            Variante.entries.toSet(),
            estrategias.map { it.variante }.toSet()
        )
    }

    @Test
    fun repartoComunitario() {
        assertEquals(
            2,
            TexasHoldem.planReparto(Calle.PREFLOP).ocultas
        )

        assertEquals(
            4,
            OmahaHiLo.planReparto(Calle.PREFLOP).ocultas
        )

        assertEquals(
            3,
            TexasHoldem.planReparto(Calle.FLOP).comunitarias
        )
    }

    @Test
    fun repartoStud() {
        val estrategias: List<ReglasVariante> =
            listOf(Razz, SevenCardStud, StudEightOrBetter)

        estrategias.forEach { reglas ->

            val planes = reglas.calles.map {
                reglas.planReparto(it)
            }

            assertEquals(3, planes.sumOf { it.ocultas })
            assertEquals(4, planes.sumOf { it.visibles })
            assertEquals(0, planes.sumOf { it.comunitarias })
        }
    }

    @Test
    fun visibilidadStud() {
        val mano = cartas("As 2d 3c 4h 5s Kd Qc")

        assertEquals(
            mano.subList(2, 6),
            SevenCardStud.cartasVisibles(mano)
        )

        assertTrue(
            TexasHoldem.cartasVisibles(mano.take(2))
                .isEmpty()
        )
    }

    @Test
    fun holdemEscaleraReal() {
        val resultado = TexasHoldem.evaluar(
            cartas("As Ks"),
            cartas("Qs Js Ts 2d 3c")
        )

        assertEquals(
            CategoriaAlta.ESCALERA_COLOR,
            resultado.alta?.categoria
        )

        assertNull(resultado.baja)
    }

    @Test
    fun omahaRespetaDosMasTres() {
        val resultado = OmahaHiLo.evaluar(
            cartas("2c 3d 4h 5c"),
            cartas("As Ks Qs Js Ts")
        )

        assertNotNull(resultado.alta)

        assertNotEquals(
            CategoriaAlta.ESCALERA_COLOR,
            resultado.alta.categoria
        )

        assertNull(resultado.baja)
    }

    @Test
    fun omahaAltaYBajaIndependientes() {
        val resultado = OmahaHiLo.evaluar(
            cartas("As 2d Kh Kc"),
            cartas("3h 4d 5s Ks Kd")
        )

        assertEquals(
            CategoriaAlta.POKER,
            resultado.alta?.categoria
        )

        assertEquals(
            listOf(5, 4, 3, 2, 1),
            resultado.baja?.desempates
        )
    }

    @Test
    fun razzNoExigeCalificador() {
        val resultado = Razz.evaluar(
            cartas("As 2d 3c 4h 9s Kd Qc"),
            emptyList()
        )

        assertNull(resultado.alta)
        assertNotNull(resultado.baja)
        assertFalse(resultado.baja.calificaOchoOMejor)
    }

    @Test
    fun studHigh() {
        val resultado = SevenCardStud.evaluar(
            cartas("As Ad Ac Ah 2s 3d 4c"),
            emptyList()
        )

        assertEquals(
            CategoriaAlta.POKER,
            resultado.alta?.categoria
        )

        assertNull(resultado.baja)
    }

    @Test
    fun studHiLo() {
        val resultado = StudEightOrBetter.evaluar(
            cartas("As 2d 3c 4h 5s Kd Qc"),
            emptyList()
        )

        assertEquals(
            CategoriaAlta.ESCALERA,
            resultado.alta?.categoria
        )

        assertEquals(
            listOf(5, 4, 3, 2, 1),
            resultado.baja?.desempates
        )
    }

    @Test
    fun limitesDeApuestas() {
        val config = ConfiguracionPartida()

        Variante.entries.forEach {
            val reglas = Variantes.obtener(it)

            assertEquals(
                config.apuestaPequena,
                reglas.apuestaBase(
                    reglas.calles.first(), config
                )
            )

            assertEquals(
                config.apuestaGrande,
                reglas.apuestaBase(
                    reglas.calles.last(), config
                )
            )
        }
    }
}