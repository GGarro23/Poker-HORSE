package cr.ac.tec.pokerhorse.core.betting

import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.engine.*
import cr.ac.tec.pokerhorse.core.variants.TexasHoldem
import kotlin.test.*

class GestorApuestasTest {
    private val gestor = GestorApuestas()
    private fun inicio(): EstadoMotor {
        val config = ConfiguracionPartida(cantidadJugadores = 3)
        return InicioMano().iniciar(config, List(3) {
            Jugador(JugadorId(it), "J$it", Asiento(it), 1000L)
        }, Asiento(0), Mazo.crear(), TexasHoldem, 0)
    }
    @Test fun callNoMutaEstado() {
        val estado = inicio()
        val ronda = estado.ronda!!
        assertTrue(Accion.Call in gestor.legales(estado.mano, ronda))
        assertFalse(Accion.Check in gestor.legales(estado.mano, ronda))
        val (mano, _) = gestor.aplicar(estado.mano, ronda, Accion.Call)
        assertEquals(50L, mano.pozo)
        assertEquals(30L, estado.mano.pozo)
        assertEquals(3000L, mano.totalFichas)
    }
    @Test fun raiseObjetivoFijo() {
        val estado = inicio()
        val ronda = estado.ronda!!
        assertTrue(Accion.Raise(40L) in gestor.legales(estado.mano, ronda))
        assertFalse(Accion.Raise(41L) in gestor.legales(estado.mano, ronda))
        val (mano, nueva) = gestor.aplicar(estado.mano, ronda, Accion.Raise(40L))
        assertEquals(1, mano.aumentosCalle)
        assertEquals(40L, nueva.objetivo)
        assertEquals(setOf(JugadorId(1), JugadorId(2)), nueva.pendientes)
    }
}
