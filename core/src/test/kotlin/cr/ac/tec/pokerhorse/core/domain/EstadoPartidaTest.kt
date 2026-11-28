package cr.ac.tec.pokerhorse.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EstadoPartidaTest {

    private val config = ConfiguracionPartida(
        cantidadJugadores = 2,
        fichasIniciales = 1000L
    )

    private val jugador1 = Jugador(
        JugadorId(1), "Gabriel", Asiento(0), 1000L
    )

    private val jugador2 = Jugador(
        JugadorId(2), "Jugador 2", Asiento(1), 1000L
    )

    private fun crearMano(
        jugadores: List<Jugador> = listOf(jugador1, jugador2),
        participaciones: List<ParticipacionMano> = listOf(
            ParticipacionMano(jugador1.id),
            ParticipacionMano(jugador2.id)
        ),
        variante: Variante = Variante.HOLD_EM
    ): EstadoMano {
        return EstadoMano(
            variante = variante,
            calle = if (variante == Variante.RAZZ) {
                Calle.TERCERA
            } else {
                Calle.PREFLOP
            },
            mazo = Mazo.crear(),
            jugadores = jugadores,
            participaciones = participaciones,
            boton = Asiento(0)
        )
    }

    @Test
    fun partidaInicialValida() {
        val partida = EstadoPartida(config, crearMano())

        assertEquals(config, partida.configuracion)
        assertEquals(0L, partida.manosCompletadas)
        assertEquals(2000L, partida.totalFichasIniciales)
        assertEquals(2, partida.obtenerJugadores().size)
    }

    @Test
    fun manosCompletadasNegativasSonInvalidas() {
        assertFailsWith<IllegalArgumentException> {
            EstadoPartida(config, crearMano(), -1L)
        }
    }

    @Test
    fun cantidadDeJugadoresInconsistente() {
        assertFailsWith<IllegalArgumentException> {
            EstadoPartida(
                config.copy(cantidadJugadores = 3),
                crearMano()
            )
        }
    }

    @Test
    fun asientoFueraDeLimitesEsInvalido() {
        val mano = crearMano(
            jugadores = listOf(
                jugador1,
                jugador2.copy(asiento = Asiento(2))
            )
        )

        assertFailsWith<IllegalArgumentException> {
            EstadoPartida(config, mano)
        }
    }

    @Test
    fun noPermiteCrearFichas() {
        val mano = crearMano(
            jugadores = listOf(
                jugador1.recibirFichas(100L),
                jugador2
            )
        )

        assertFailsWith<IllegalArgumentException> {
            EstadoPartida(config, mano)
        }
    }

    @Test
    fun noPermitePerderFichas() {
        val mano = crearMano(
            jugadores = listOf(
                jugador1.retirarFichas(100L),
                jugador2
            )
        )

        assertFailsWith<IllegalArgumentException> {
            EstadoPartida(config, mano)
        }
    }

    @Test
    fun aportesConservanTotalDeLaPartida() {
        val aporte = ParticipacionMano(jugador1.id)
            .aportarFichas(jugador1, 300L)

        val mano = crearMano(
            jugadores = listOf(aporte.jugador, jugador2),
            participaciones = listOf(
                aporte.participacion,
                ParticipacionMano(jugador2.id)
            )
        )

        val partida = EstadoPartida(config, mano)

        assertEquals(2000L, partida.totalFichasIniciales)
        assertEquals(2000L, partida.manoActual.totalFichas)
        assertEquals(300L, partida.manoActual.pozo)
    }

    @Test
    fun varianteActualCorrespondeALaMano() {
        val partida = EstadoPartida(
            config,
            crearMano(variante = Variante.RAZZ)
        )

        assertEquals(Variante.RAZZ, partida.varianteActual)
    }

    @Test
    fun actualizarManosNoModificaOriginal() {
        val original = EstadoPartida(config, crearMano())
        val actualizado = original.copy(manosCompletadas = 1L)

        assertEquals(0L, original.manosCompletadas)
        assertEquals(1L, actualizado.manosCompletadas)
    }

    @Test
    fun jugadorEliminadoPermaneceRegistrado() {
        val mano = crearMano(
            jugadores = listOf(
                jugador1.copy(fichas = 0L),
                jugador2.copy(fichas = 2000L)
            ),
            participaciones = emptyList()
        )

        val partida = EstadoPartida(config, mano)

        assertEquals(2, partida.obtenerJugadores().size)
        assertEquals(0L, partida.obtenerJugadores().first().fichas)
        assertEquals(2000L, partida.manoActual.totalFichas)
    }
}