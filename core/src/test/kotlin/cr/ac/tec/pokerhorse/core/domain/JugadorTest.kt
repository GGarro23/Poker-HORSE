package cr.ac.tec.pokerhorse.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertFailsWith

class JugadorTest {

    private fun crearJugador(fichas: Long = 1000L): Jugador {
        return Jugador(
            JugadorId(1),
            "Gabriel",
            Asiento(0),
            fichas
        )
    }

    @Test
    fun jugadorValido() {
        val jugador = crearJugador()

        assertEquals(JugadorId(1), jugador.id)
        assertEquals("Gabriel", jugador.nombre)
        assertEquals(Asiento(0), jugador.asiento)
        assertEquals(1000L, jugador.fichas)
    }

    @Test
    fun jugadorSinFichasEsValido() {
        assertEquals(0L, crearJugador(0L).fichas)
    }

    @Test
    fun jugadorConFichasNegativasEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            crearJugador(-1L)
        }
    }

    @Test
    fun jugadorConNombreVacioEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            crearJugador().copy(nombre = "")
        }
    }

    @Test
    fun jugadorConNombreEnBlancoEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            crearJugador().copy(nombre = "   ")
        }
    }

    @Test
    fun retirarFichas() {
        val jugador = crearJugador()
        val resultado = jugador.retirarFichas(300L)

        assertEquals(700L, resultado.fichas)
        assertEquals(1000L, jugador.fichas)
    }

    @Test
    fun retirarTodasLasFichas() {
        val jugador = crearJugador()

        assertEquals(
            0L,
            jugador.retirarFichas(1000L).fichas
        )
    }

    @Test
    fun retirarCeroFichas() {
        val jugador = crearJugador()

        assertEquals(jugador, jugador.retirarFichas(0L))
    }

    @Test
    fun retirarCantidadNegativa() {
        assertFailsWith<IllegalArgumentException> {
            crearJugador().retirarFichas(-100L)
        }
    }

    @Test
    fun retirarMasFichasDeLasDisponibles() {
        assertFailsWith<IllegalArgumentException> {
            crearJugador().retirarFichas(1500L)
        }
    }

    @Test
    fun recibirFichas() {
        val jugador = crearJugador()
        val resultado = jugador.recibirFichas(500L)

        assertEquals(1500L, resultado.fichas)
        assertEquals(1000L, jugador.fichas)
    }

    @Test
    fun recibirCeroFichas() {
        val jugador = crearJugador()

        assertEquals(jugador, jugador.recibirFichas(0L))
    }

    @Test
    fun recibirCantidadNegativa() {
        assertFailsWith<IllegalArgumentException> {
            crearJugador().recibirFichas(-100L)
        }
    }

    @Test
    fun recibirHastaElMaximoPermitido() {
        val jugador = crearJugador(Long.MAX_VALUE - 1L)

        assertEquals(
            Long.MAX_VALUE,
            jugador.recibirFichas(1L).fichas
        )
    }

    @Test
    fun recibirFichasConDesbordamiento() {
        assertFailsWith<IllegalArgumentException> {
            crearJugador(Long.MAX_VALUE).recibirFichas(1L)
        }
    }

    @Test
    fun operacionesConservanIdentidad() {
        val jugador = crearJugador()
        val retirado = jugador.retirarFichas(300L)
        val recibido = jugador.recibirFichas(500L)

        for (resultado in listOf(retirado, recibido)) {
            assertEquals(jugador.id, resultado.id)
            assertEquals(jugador.nombre, resultado.nombre)
            assertEquals(jugador.asiento, resultado.asiento)
            assertNotEquals(jugador, resultado)
        }
    }

    @Test
    fun retirarYRecibirRestauranEstado() {
        val jugador = crearJugador()

        val resultado = jugador
            .retirarFichas(300L)
            .recibirFichas(300L)

        assertEquals(jugador, resultado)
    }
}