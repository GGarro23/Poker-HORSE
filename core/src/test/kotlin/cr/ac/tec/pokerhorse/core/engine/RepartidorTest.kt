package cr.ac.tec.pokerhorse.core.engine

import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.variants.*
import kotlin.test.*

class RepartidorTest {
    private val repartidor = Repartidor()
    private fun mano(reglas: ReglasVariante, n: Int = 3): EstadoMano {
        val jugadores = List(n) { Jugador(JugadorId(it), "J$it", Asiento(it), 1000L) }
        return EstadoMano(reglas.variante, reglas.calles.first(), Mazo.crear(), jugadores,
            jugadores.map { ParticipacionMano(it.id) }, boton = Asiento(0))
    }

    @Test fun repartoCircular() {
        val antes = mano(TexasHoldem)
        val cartas = antes.mazo.obtenerCartas()
        val despues = repartidor.repartir(antes, Calle.PREFLOP, TexasHoldem)
        assertEquals(listOf(cartas[0], cartas[3]), despues.buscarParticipacion(JugadorId(1))!!.obtenerCartas())
        assertEquals(listOf(cartas[2], cartas[5]), despues.buscarParticipacion(JugadorId(0))!!.obtenerCartas())
        assertEquals(46, despues.mazo.cantidad)
        assertEquals(52, antes.mazo.cantidad)
        assertEquals(antes.totalFichas, despues.totalFichas)
        assertTrue(antes.obtenerParticipaciones().all { it.obtenerCartas().isEmpty() })
    }

    @Test fun cincoEstrategias() {
        for (variante in Variante.entries) {
            val reglas = Variantes.obtener(variante)
            val estado = repartidor.repartir(mano(reglas), reglas.calles.first(), reglas)
            val plan = reglas.planReparto(reglas.calles.first())
            assertTrue(estado.obtenerParticipaciones().all {
                it.obtenerCartas().size == plan.ocultas + plan.visibles
            })
            assertEquals(52 - 3 * (plan.ocultas + plan.visibles), estado.mazo.cantidad)
        }
    }

    @Test fun flopQuemaUnaCarta() {
        val pre = repartidor.repartir(mano(TexasHoldem), Calle.PREFLOP, TexasHoldem)
        val flop = repartidor.repartir(pre, Calle.FLOP, TexasHoldem)
        assertEquals(pre.mazo.obtenerCartas().drop(1).take(3), flop.obtenerComunitarias())
        assertEquals(42, flop.mazo.cantidad)
    }

    @Test fun studOchoJugadores() {
        var estado = mano(SevenCardStud, 8)
        for (calle in SevenCardStud.calles) estado = repartidor.repartir(estado, calle, SevenCardStud)
        assertEquals(0, estado.mazo.cantidad)
        assertEquals(1, estado.obtenerComunitarias().size)
        assertTrue(estado.obtenerParticipaciones().all { it.obtenerCartas().size == 6 })
    }

    @Test fun allInRecibeCartasRetiradoNo() {
        val original = mano(SevenCardStud)
        val estado = EstadoMano(original.variante, original.calle, original.mazo,
            original.obtenerJugadores().map { if (it.id == JugadorId(1)) it.copy(fichas = 0L) else it }, original.obtenerParticipaciones().map {
                when (it.jugadorId) {
                    JugadorId(0) -> it.cambiarEstado(EstadoJugador.RETIRADO)
                    JugadorId(1) -> ParticipacionMano(it.jugadorId, aporteTotal = 1000L, estado = EstadoJugador.ALL_IN)
                    else -> it
                }
            }, boton = original.boton)
        val repartida = repartidor.repartir(estado, Calle.TERCERA, SevenCardStud)
        assertEquals(0, repartida.buscarParticipacion(JugadorId(0))!!.obtenerCartas().size)
        assertEquals(3, repartida.buscarParticipacion(JugadorId(1))!!.obtenerCartas().size)
    }
}
