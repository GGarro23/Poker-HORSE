package cr.ac.tec.pokerhorse.core.engine

import cr.ac.tec.pokerhorse.core.domain.*
import kotlin.test.*

class SesionAutoritativaTest {
    @Test fun rechazaDuplicadosYAccionesFueraDeTurno() {
        val motor = MotorDeJuego()
        val inicial = motor.iniciar(ConfiguracionPartida(cantidadJugadores = 3),
            listOf("A", "B", "C"), Mazo.crear())
        val sesion = SesionAutoritativa(motor, inicial, JugadorId(0))
        val actor = inicial.mano.turno!!
        val otro = inicial.mano.obtenerJugadores().first { it.id != actor }.id
        assertEquals(RespuestaComando.Rechazado(ErrorDominio.FUERA_DE_TURNO),
            sesion.aplicar(otro, 0, Accion.Call))
        assertEquals(0L, sesion.vista(actor).revision)
        assertEquals(RespuestaComando.Aceptado(1), sesion.aplicar(actor, 0, Accion.Call))
        assertEquals(RespuestaComando.RevisionObsoleta, sesion.aplicar(actor, 0, Accion.Call))
        assertEquals(1L, sesion.vista(otro).revision)
    }
}
