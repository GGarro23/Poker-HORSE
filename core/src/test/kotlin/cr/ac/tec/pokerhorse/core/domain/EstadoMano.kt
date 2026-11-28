package cr.ac.tec.pokerhorse.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertFailsWith

class EstadoManoTest {

    private val jugador1 = Jugador(
        JugadorId(1), "Gabriel", Asiento(0), 1000L
    )

    private val jugador2 = Jugador(
        JugadorId(2), "Jugador 2", Asiento(1), 1000L
    )

    private fun crearEstado(
        jugadores: List<Jugador> = listOf(jugador1, jugador2),
        participaciones: List<ParticipacionMano> = listOf(
            ParticipacionMano(jugador1.id),
            ParticipacionMano(jugador2.id)
        ),
        mazo: Mazo = Mazo.crear(),
        comunitarias: List<Carta> = emptyList(),
        turno: JugadorId? = jugador1.id,
        boton: Asiento = Asiento(0),
        aumentosCalle: Int = 0
    ): EstadoMano {
        return EstadoMano(
            variante = Variante.HOLD_EM,
            calle = Calle.PREFLOP,
            mazo = mazo,
            jugadores = jugadores,
            participaciones = participaciones,
            comunitarias = comunitarias,
            turno = turno,
            boton = boton,
            aumentosCalle = aumentosCalle
        )
    }

    @Test
    fun estadoInicialValido() {
        val estado = crearEstado()

        assertEquals(Variante.HOLD_EM, estado.variante)
        assertEquals(Calle.PREFLOP, estado.calle)
        assertEquals(52, estado.mazo.cantidad)
        assertEquals(2, estado.obtenerJugadores().size)
        assertEquals(2, estado.obtenerParticipaciones().size)
        assertEquals(jugador1.id, estado.turno)
        assertEquals(0L, estado.pozo)
        assertEquals(0L, estado.apuestaVigente)
        assertEquals(2000L, estado.totalFichas)
    }

    @Test
    fun identidadesDuplicadasSonInvalidas() {
        assertFailsWith<IllegalArgumentException> {
            crearEstado(
                jugadores = listOf(
                    jugador1,
                    jugador2.copy(id = jugador1.id)
                )
            )
        }
    }

    @Test
    fun asientosDuplicadosSonInvalidos() {
        assertFailsWith<IllegalArgumentException> {
            crearEstado(
                jugadores = listOf(
                    jugador1,
                    jugador2.copy(asiento = jugador1.asiento)
                )
            )
        }
    }

    @Test
    fun participacionesDuplicadasSonInvalidas() {
        assertFailsWith<IllegalArgumentException> {
            crearEstado(
                participaciones = listOf(
                    ParticipacionMano(jugador1.id),
                    ParticipacionMano(jugador1.id)
                )
            )
        }
    }

    @Test
    fun participacionSinJugadorEsInvalida() {
        assertFailsWith<IllegalArgumentException> {
            crearEstado(
                participaciones = listOf(
                    ParticipacionMano(jugador1.id),
                    ParticipacionMano(JugadorId(99))
                )
            )
        }
    }

    @Test
    fun turnoInexistenteEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            crearEstado(turno = JugadorId(99))
        }
    }

    @Test
    fun turnoDeJugadorRetiradoEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            crearEstado(
                participaciones = listOf(
                    ParticipacionMano(
                        jugador1.id,
                        estado = EstadoJugador.RETIRADO
                    ),
                    ParticipacionMano(jugador2.id)
                )
            )
        }
    }

    @Test
    fun turnoDeJugadorSinFichasEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            crearEstado(
                jugadores = listOf(
                    jugador1.copy(fichas = 0L),
                    jugador2
                )
            )
        }
    }

    @Test
    fun turnoNuloEsValido() {
        assertNull(crearEstado(turno = null).turno)
    }

    @Test
    fun botonInexistenteEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            crearEstado(boton = Asiento(99))
        }
    }

    @Test
    fun aumentosNegativosSonInvalidos() {
        assertFailsWith<IllegalArgumentException> {
            crearEstado(aumentosCalle = -1)
        }
    }

    @Test
    fun pozoYApuestaVigenteSeCalculan() {
        val resultado = ParticipacionMano(jugador1.id)
            .aportarFichas(jugador1, 300L)

        val estado = crearEstado(
            jugadores = listOf(resultado.jugador, jugador2),
            participaciones = listOf(
                resultado.participacion,
                ParticipacionMano(jugador2.id)
            )
        )

        assertEquals(300L, estado.pozo)
        assertEquals(300L, estado.apuestaVigente)
        assertEquals(2000L, estado.totalFichas)
    }

    @Test
    fun desbordamientoGlobalEsInvalido() {
        assertFailsWith<IllegalArgumentException> {
            crearEstado(
                jugadores = listOf(
                    jugador1.copy(fichas = Long.MAX_VALUE),
                    jugador2
                )
            )
        }
    }

    @Test
    fun cartaRepartidaNoPuedePermanecerEnMazo() {
        val mazo = Mazo.crear()
        val carta = mazo.extraer().carta

        assertFailsWith<IllegalArgumentException> {
            crearEstado(
                mazo = mazo,
                comunitarias = listOf(carta)
            )
        }
    }

    @Test
    fun cartaExtraidaPuedeRepartirse() {
        val resultado = Mazo.crear().extraer()

        val estado = crearEstado(
            mazo = resultado.mazoRestante,
            participaciones = listOf(
                ParticipacionMano(
                    jugador1.id,
                    listOf(resultado.carta)
                ),
                ParticipacionMano(jugador2.id)
            )
        )

        assertEquals(51, estado.mazo.cantidad)

        assertEquals(
            listOf(resultado.carta),
            estado.buscarParticipacion(jugador1.id)
                ?.obtenerCartas()
        )
    }

    @Test
    fun cartaDuplicadaEntreJugadoresEsInvalida() {
        val resultado = Mazo.crear().extraer()

        assertFailsWith<IllegalArgumentException> {
            crearEstado(
                mazo = resultado.mazoRestante,
                participaciones = listOf(
                    ParticipacionMano(
                        jugador1.id,
                        listOf(resultado.carta)
                    ),
                    ParticipacionMano(
                        jugador2.id,
                        listOf(resultado.carta)
                    )
                )
            )
        }
    }

    @Test
    fun protegeColeccionesRecibidas() {
        val jugadores = mutableListOf(jugador1, jugador2)

        val participaciones = mutableListOf(
            ParticipacionMano(jugador1.id),
            ParticipacionMano(jugador2.id)
        )

        val estado = crearEstado(
            jugadores = jugadores,
            participaciones = participaciones
        )

        jugadores.clear()
        participaciones.clear()

        assertEquals(2, estado.obtenerJugadores().size)
        assertEquals(2, estado.obtenerParticipaciones().size)
    }

    @Test
    fun protegeColeccionesDevueltas() {
        val estado = crearEstado()

        val jugadores = estado.obtenerJugadores()
        val participaciones = estado.obtenerParticipaciones()

        if (jugadores is MutableList<Jugador>) {
            jugadores.clear()
        }

        if (participaciones is MutableList<ParticipacionMano>) {
            participaciones.clear()
        }

        assertEquals(2, estado.obtenerJugadores().size)
        assertEquals(2, estado.obtenerParticipaciones().size)
    }

    @Test
    fun buscarJugadorPorIdentidad() {
        val estado = crearEstado()

        assertEquals(jugador1, estado.buscarJugador(jugador1.id))
        assertNull(estado.buscarJugador(JugadorId(99)))
    }
}