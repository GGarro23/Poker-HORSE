package cr.ac.tec.pokerhorse.core.engine

import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.bots.BotBasico
import kotlin.test.*

class VistaJugadorTest {
    @Test fun soloActorTieneOpcionesYCartasPrivadas() {
        val motor = MotorDeJuego()
        val estado = motor.iniciar(ConfiguracionPartida(cantidadJugadores = 3),
            listOf("A", "B", "C"), Mazo.crear())
        val actor = estado.mano.turno!!
        val vista = motor.vistaPara(estado, actor)
        assertEquals(2, vista.propias.size)
        assertTrue(vista.participantes.all { it.cartasVisibles.isEmpty() })
        assertTrue(vista.accionesLegales.isNotEmpty())
        val otro = estado.mano.obtenerJugadores().first { it.id != actor }.id
        assertTrue(motor.vistaPara(estado, otro).accionesLegales.isEmpty())
        val propuesta = BotBasico().decidir(vista)!!
        assertTrue(propuesta in vista.accionesLegales)
        assertIs<ResultadoTransicion.Exito<EstadoMotor>>(motor.aplicar(estado, actor, propuesta))
    }
}
