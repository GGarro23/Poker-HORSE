package cr.ac.tec.pokerhorse.core.engine

import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.variants.*
import kotlin.test.*

class InicioManoTest {
    private val config = ConfiguracionPartida(cantidadJugadores = 3)
    private val jugadores = List(3) { Jugador(JugadorId(it), "J$it", Asiento(it), 1000L) }
    @Test fun ciegasYTurno() {
        val estado = InicioMano().iniciar(config, jugadores, Asiento(0), Mazo.crear(), TexasHoldem, 0)
        assertEquals(30L, estado.mano.pozo)
        assertEquals(JugadorId(0), estado.mano.turno)
        assertEquals(20L, estado.ronda!!.objetivo)
        assertEquals(3000L, estado.mano.totalFichas)
    }

    @Test fun antesYBringIn() {
        val estado = InicioMano().iniciar(config, jugadores, Asiento(0), Mazo.crear(), SevenCardStud, 0)
        assertEquals(11L, estado.mano.pozo)
        assertEquals(5L, estado.mano.apuestaVigente)
        assertFalse(estado.ronda!!.abierta)
        assertEquals(3000L, estado.mano.totalFichas)
        assertEquals(1000L, jugadores[0].fichas)
    }
}
