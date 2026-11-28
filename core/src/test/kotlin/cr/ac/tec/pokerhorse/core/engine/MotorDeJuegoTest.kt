package cr.ac.tec.pokerhorse.core.engine

import cr.ac.tec.pokerhorse.core.domain.*
import kotlin.test.*

class MotorDeJuegoTest {
    private val motor = MotorDeJuego()
    private fun inicio(n: Int = 3, manos: Int = 6) = motor.iniciar(
        ConfiguracionPartida(cantidadJugadores = n, manosPorVariante = manos),
        List(n) { "J$it" }, Mazo.crear())

    private fun avanzar(estado: EstadoMotor, accion: Accion): EstadoMotor =
        assertIs<ResultadoTransicion.Exito<EstadoMotor>>(
            motor.aplicar(estado, estado.mano.turno!!, accion)).estado

    @Test fun rechazosSinMutaciones() {
        val estado = inicio()
        assertEquals(ResultadoTransicion.Rechazo(ErrorDominio.FUERA_DE_TURNO),
            motor.aplicar(estado, JugadorId(1), Accion.Fold))
        assertEquals(ResultadoTransicion.Rechazo(ErrorDominio.ACCION_NO_PERMITIDA),
            motor.aplicar(estado, JugadorId(0), Accion.Check))
        assertEquals(30L, estado.mano.pozo)
        assertEquals(JugadorId(0), estado.mano.turno)
    }

    @Test fun ciegaGrandeConservaOpcion() {
        var estado = inicio()
        estado = avanzar(estado, Accion.Call)
        estado = avanzar(estado, Accion.Call)
        assertEquals(Calle.PREFLOP, estado.mano.calle)
        assertEquals(JugadorId(2), estado.mano.turno)
        assertTrue(Accion.Check in motor.accionesLegales(estado))
        estado = avanzar(estado, Accion.Check)
        assertEquals(Calle.FLOP, estado.mano.calle)
        assertEquals(60L, estado.mano.pozo)
        assertEquals(0L, estado.mano.apuestaVigente)
    }

    @Test fun foldsLiquidanYDevuelvenExceso() {
        val original = inicio()
        var estado = avanzar(original, Accion.Fold)
        estado = avanzar(estado, Accion.Fold)
        assertTrue(estado.finalizada)
        assertEquals(20L, estado.resultado!!.premios.sumOf { it.cantidad })
        assertEquals(1010L, estado.mano.buscarJugador(JugadorId(2))!!.fichas)
        assertEquals(3000L, estado.mano.totalFichas)
        assertEquals(0L, estado.mano.pozo)
        assertEquals(1L, estado.partida.manosCompletadas)
        assertEquals(30L, original.mano.pozo)
    }

    @Test fun showdownYRotacionCompleta() {
        var estado = inicio(2, 1)
        val esperadas = listOf(Variante.HOLD_EM, Variante.OMAHA_HI_LO, Variante.RAZZ,
            Variante.STUD, Variante.STUD_HI_LO, Variante.HOLD_EM)
        for ((indice, variante) in esperadas.withIndex()) {
            assertEquals(variante, estado.mano.variante)
            var pasos = 0
            while (!estado.finalizada) {
                assertTrue(++pasos < 100)
                val legales = motor.accionesLegales(estado)
                estado = avanzar(estado, if (Accion.Check in legales) Accion.Check else Accion.Call)
                assertEquals(2000L, estado.mano.totalFichas)
            }
            assertEquals(indice.toLong() + 1, estado.partida.manosCompletadas)
            if (indice != esperadas.lastIndex) {
                estado = assertIs<ResultadoTransicion.Exito<EstadoMotor>>(
                    motor.siguienteMano(estado, Mazo.crear())).estado
            }
        }
    }
}
