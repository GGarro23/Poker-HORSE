package cr.ac.tec.pokerhorse.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class ParticipacionManoTest {

    private val id = JugadorId(1)
    private val carta1 = Carta(Palo.PICA, Rango.AS)
    private val carta2 = Carta(Palo.CORAZON, Rango.REY)

    private fun crearJugador(fichas: Long = 1000L): Jugador {
        return Jugador(id, "Gabriel", Asiento(0), fichas)
    }

    @Test
    fun participacionInicialValida() {
        val participacion = ParticipacionMano(id)

        assertEquals(id, participacion.jugadorId)
        assertEquals(0L, participacion.aporteCalle)
        assertEquals(0L, participacion.aporteTotal)
        assertEquals(EstadoJugador.ACTIVO, participacion.estado)
        assertTrue(participacion.obtenerCartas().isEmpty())
    }

    @Test
    fun aporteCalleNegativoEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            ParticipacionMano(id, aporteCalle = -1L)
        }
    }

    @Test
    fun aporteTotalNegativoEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            ParticipacionMano(id, aporteTotal = -1L)
        }
    }

    @Test
    fun aporteCalleSuperiorAlTotalEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            ParticipacionMano(
                id,
                aporteCalle = 200L,
                aporteTotal = 100L
            )
        }
    }

    @Test
    fun cartasDuplicadasSonInvalidas() {
        assertFailsWith<IllegalArgumentException> {
            ParticipacionMano(id, listOf(carta1, carta1))
        }
    }

    @Test
    fun protegeColeccionRecibida() {
        val cartas = mutableListOf(carta1, carta2)
        val participacion = ParticipacionMano(id, cartas)

        cartas.clear()

        assertEquals(
            listOf(carta1, carta2),
            participacion.obtenerCartas()
        )
    }

    @Test
    fun protegeColeccionDevuelta() {
        val participacion = ParticipacionMano(
            id, listOf(carta1, carta2)
        )

        val cartas = participacion.obtenerCartas()

        if (cartas is MutableList<Carta>) {
            cartas.clear()
        }

        assertEquals(
            listOf(carta1, carta2),
            participacion.obtenerCartas()
        )
    }

    @Test
    fun agregarCartaNoModificaOriginal() {
        val original = ParticipacionMano(id, listOf(carta1))
        val resultado = original.agregarCarta(carta2)

        assertEquals(listOf(carta1), original.obtenerCartas())
        assertEquals(
            listOf(carta1, carta2),
            resultado.obtenerCartas()
        )
    }

    @Test
    fun agregarCartaDuplicadaEsInvalido() {
        val participacion = ParticipacionMano(id, listOf(carta1))

        assertFailsWith<IllegalArgumentException> {
            participacion.agregarCarta(carta1)
        }
    }

    @Test
    fun aporteActualizaAmbosObjetos() {
        val jugador = crearJugador()
        val participacion = ParticipacionMano(id)

        val resultado = participacion.aportarFichas(jugador, 300L)

        assertEquals(700L, resultado.jugador.fichas)
        assertEquals(300L, resultado.participacion.aporteCalle)
        assertEquals(300L, resultado.participacion.aporteTotal)
    }

    @Test
    fun aporteNoModificaOriginales() {
        val jugador = crearJugador()
        val participacion = ParticipacionMano(id)

        participacion.aportarFichas(jugador, 300L)

        assertEquals(1000L, jugador.fichas)
        assertEquals(0L, participacion.aporteCalle)
        assertEquals(0L, participacion.aporteTotal)
    }

    @Test
    fun aporteConservaFichas() {
        val jugador = crearJugador(1000L)
        val participacion = ParticipacionMano(id)

        val resultado = participacion.aportarFichas(jugador, 350L)

        assertEquals(
            jugador.fichas + participacion.aporteTotal,
            resultado.jugador.fichas +
                    resultado.participacion.aporteTotal
        )
    }

    @Test
    fun aportesAcumuladosConservanFichas() {
        val jugador = crearJugador()
        val participacion = ParticipacionMano(id)

        val primero = participacion.aportarFichas(jugador, 200L)

        val segundo = primero.participacion.aportarFichas(
            primero.jugador, 300L
        )

        assertEquals(500L, segundo.jugador.fichas)
        assertEquals(500L, segundo.participacion.aporteCalle)
        assertEquals(500L, segundo.participacion.aporteTotal)
    }

    @Test
    fun anteSoloIncrementaAporteTotal() {
        val resultado = ParticipacionMano(id).aportarFichas(
            crearJugador(), 50L, esAnte = true
        )

        assertEquals(950L, resultado.jugador.fichas)
        assertEquals(0L, resultado.participacion.aporteCalle)
        assertEquals(50L, resultado.participacion.aporteTotal)
    }

    @Test
    fun reiniciarCalleConservaAporteTotal() {
        val original = ParticipacionMano(
            id,
            aporteCalle = 100L,
            aporteTotal = 300L
        )

        val resultado = original.reiniciarAporteCalle()

        assertEquals(0L, resultado.aporteCalle)
        assertEquals(300L, resultado.aporteTotal)
        assertEquals(100L, original.aporteCalle)
    }

    @Test
    fun aporteNegativoEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            ParticipacionMano(id).aportarFichas(
                crearJugador(), -1L
            )
        }
    }

    @Test
    fun aporteSuperiorAFichasEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            ParticipacionMano(id).aportarFichas(
                crearJugador(), 1500L
            )
        }
    }

    @Test
    fun aporteDeOtroJugadorEsInvalido() {
        val otroJugador = crearJugador().copy(id = JugadorId(2))

        assertFailsWith<IllegalArgumentException> {
            ParticipacionMano(id).aportarFichas(
                otroJugador, 100L
            )
        }
    }

    @Test
    fun retirarParticipacionConservaAportes() {
        val original = ParticipacionMano(
            id,
            aporteCalle = 100L,
            aporteTotal = 200L
        )

        val resultado = original.cambiarEstado(
            EstadoJugador.RETIRADO
        )

        assertEquals(EstadoJugador.RETIRADO, resultado.estado)
        assertEquals(100L, resultado.aporteCalle)
        assertEquals(200L, resultado.aporteTotal)
        assertEquals(EstadoJugador.ACTIVO, original.estado)
    }

    @Test
    fun jugadorRetiradoNoPuedeAportar() {
        val participacion = ParticipacionMano(
            id, estado = EstadoJugador.RETIRADO
        )

        assertFailsWith<IllegalArgumentException> {
            participacion.aportarFichas(crearJugador(), 100L)
        }
    }

    @Test
    fun aportarTodasLasFichasProduceAllIn() {
        val resultado = ParticipacionMano(id).aportarFichas(
            crearJugador(), 1000L
        )

        assertEquals(0L, resultado.jugador.fichas)
        assertEquals(1000L, resultado.participacion.aporteTotal)
        assertEquals(
            EstadoJugador.ALL_IN,
            resultado.participacion.estado
        )
    }

    @Test
    fun jugadorAllInNoPuedeAportar() {
        val participacion = ParticipacionMano(
            id, estado = EstadoJugador.ALL_IN
        )

        assertFailsWith<IllegalArgumentException> {
            participacion.aportarFichas(crearJugador(0L), 0L)
        }
    }

    @Test
    fun aporteConDesbordamientoEsInvalido() {
        val participacion = ParticipacionMano(
            id, aporteTotal = Long.MAX_VALUE
        )

        assertFailsWith<IllegalArgumentException> {
            participacion.aportarFichas(crearJugador(), 1L)
        }
    }
}