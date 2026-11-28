package cr.ac.tec.pokerhorse.core.betting

import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.variants.TexasHoldem
import kotlin.test.*

class CalculadorDeBotesTest {
    private val calculador = CalculadorDeBotes()
    private fun mano(aportes: List<Long>, retirados: Set<Int> = emptySet()): EstadoMano {
        val jugadores = aportes.indices.map { Jugador(JugadorId(it), "J$it", Asiento(it), 1000L - aportes[it]) }
        return EstadoMano(Variante.HOLD_EM, Calle.PREFLOP, Mazo.crear(), jugadores,
            aportes.mapIndexed { i, monto -> ParticipacionMano(JugadorId(i), aporteCalle = monto,
                aporteTotal = monto, estado = if (i in retirados) EstadoJugador.RETIRADO else EstadoJugador.ACTIVO) },
            boton = Asiento(0))
    }
    @Test fun principalSecundarioYExcedente() {
        val original = mano(listOf(100, 200, 500))
        val normal = calculador.devolverExcedente(original)
        assertEquals(800L, normal.buscarJugador(JugadorId(2))!!.fichas)
        assertEquals(500L, normal.pozo)
        assertEquals(listOf(300L, 200L), calculador.construir(normal).map { it.cantidad })
        assertEquals(original.totalFichas, normal.totalFichas)
        assertEquals(800L, original.pozo)
    }
    @Test fun retiradosAportanPeroNoGanan() {
        val estado = mano(listOf(100, 100, 100), setOf(1))
        assertEquals(Bote(300L, setOf(JugadorId(0), JugadorId(2))), calculador.construir(estado).single())
    }
    @Test fun foldNoEvaluaCartas() {
        val estado = mano(listOf(100, 100, 100), setOf(0, 1))
        val premios = calculador.distribuir(estado, TexasHoldem)
        assertEquals(JugadorId(2), premios.single().jugadorId)
        assertEquals(300L, premios.single().cantidad)
    }
}
