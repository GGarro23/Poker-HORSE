package cr.ac.tec.pokerhorse.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConfiguracionPartidaTest {

    @Test
    fun configuracionPredeterminadaValida() {
        val config = ConfiguracionPartida()

        assertEquals(6, config.cantidadJugadores)
        assertEquals(1000L, config.fichasIniciales)
        assertEquals(20L, config.apuestaPequena)
        assertEquals(40L, config.apuestaGrande)
        assertEquals(2L, config.ante)
        assertEquals(5L, config.bringIn)
        assertEquals(6, config.manosPorVariante)
    }

    @Test
    fun cantidadMinimaDeJugadores() {
        assertEquals(
            2,
            ConfiguracionPartida(cantidadJugadores = 2)
                .cantidadJugadores
        )
    }

    @Test
    fun cantidadMaximaDeJugadores() {
        assertEquals(
            8,
            ConfiguracionPartida(cantidadJugadores = 8)
                .cantidadJugadores
        )
    }

    @Test
    fun cantidadInsuficienteDeJugadores() {
        assertFailsWith<IllegalArgumentException> {
            ConfiguracionPartida(cantidadJugadores = 1)
        }
    }

    @Test
    fun demasiadosJugadores() {
        assertFailsWith<IllegalArgumentException> {
            ConfiguracionPartida(cantidadJugadores = 9)
        }
    }

    @Test
    fun fichasInicialesInvalidas() {
        for (cantidad in listOf(0L, -1L)) {
            assertFailsWith<IllegalArgumentException> {
                ConfiguracionPartida(fichasIniciales = cantidad)
            }
        }
    }

    @Test
    fun totalDeFichasSinDesbordamiento() {
        val config = ConfiguracionPartida(
            cantidadJugadores = 2,
            fichasIniciales = Long.MAX_VALUE / 2
        )

        assertEquals(
            Long.MAX_VALUE - 1L,
            config.fichasIniciales * config.cantidadJugadores
        )
    }

    @Test
    fun totalDeFichasConDesbordamiento() {
        assertFailsWith<IllegalArgumentException> {
            ConfiguracionPartida(
                cantidadJugadores = 2,
                fichasIniciales = Long.MAX_VALUE
            )
        }
    }

    @Test
    fun apuestaPequenaInvalida() {
        for (cantidad in listOf(0L, -1L)) {
            assertFailsWith<IllegalArgumentException> {
                ConfiguracionPartida(apuestaPequena = cantidad)
            }
        }
    }

    @Test
    fun apuestaGrandeInferiorALaPequena() {
        assertFailsWith<IllegalArgumentException> {
            ConfiguracionPartida(
                apuestaPequena = 20L,
                apuestaGrande = 10L
            )
        }
    }

    @Test
    fun anteCeroEsValido() {
        assertEquals(
            0L,
            ConfiguracionPartida(ante = 0L).ante
        )
    }

    @Test
    fun anteNegativoEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            ConfiguracionPartida(ante = -1L)
        }
    }

    @Test
    fun bringInNoPositivoEsInvalido() {
        for (cantidad in listOf(0L, -1L)) {
            assertFailsWith<IllegalArgumentException> {
                ConfiguracionPartida(bringIn = cantidad)
            }
        }
    }

    @Test
    fun bringInSuperiorALaApuestaPequena() {
        assertFailsWith<IllegalArgumentException> {
            ConfiguracionPartida(
                apuestaPequena = 20L,
                bringIn = 30L
            )
        }
    }

    @Test
    fun manosPorVarianteInvalidas() {
        for (cantidad in listOf(0, -1)) {
            assertFailsWith<IllegalArgumentException> {
                ConfiguracionPartida(manosPorVariante = cantidad)
            }
        }
    }

    @Test
    fun copiaNoModificaConfiguracionOriginal() {
        val original = ConfiguracionPartida()
        val copia = original.copy(fichasIniciales = 2000L)

        assertEquals(1000L, original.fichasIniciales)
        assertEquals(2000L, copia.fichasIniciales)
    }
}