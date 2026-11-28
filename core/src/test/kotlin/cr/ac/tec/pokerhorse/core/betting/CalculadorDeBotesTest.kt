package cr.ac.tec.pokerhorse.core.betting

import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.variants.*
import cr.ac.tec.pokerhorse.core.evaluation.*
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

    @Test fun splitYFichaImparPorBote() {
        val origen = mano(listOf(101L, 101L, 101L))
        // Resultados deterministas: dos high empatados y un low.
        val alto = ValorAlto(CategoriaAlta.PAREJA, listOf(10, 7, 5, 2))
        val bajo = ValorBajo(CategoriaBaja.SIN_PAREJA, listOf(8, 6, 4, 2, 1))
        val cartas = origen.mazo.obtenerCartas().take(3)
        var mazo = origen.mazo
        repeat(3) { mazo = mazo.extraer().mazoRestante }
        val estado = EstadoMano(origen.variante, origen.calle, mazo, origen.obtenerJugadores(),
            origen.obtenerParticipaciones().mapIndexed { i, part -> part.agregarCarta(cartas[i]) },
            boton = origen.boton)
        val reglas = object : ReglasVariante by TexasHoldem {
            override val modalidad = ModalidadEvaluacion.ALTA_Y_BAJA
            override fun evaluar(propias: List<Carta>, comunitarias: List<Carta>): ResultadoEvaluacion =
                if (propias.single() == cartas[2]) ResultadoEvaluacion(baja = bajo)
                else ResultadoEvaluacion(alta = alto)
        }
        val premios = calculador.distribuir(estado, reglas).groupBy { it.jugadorId }
            .mapValues { (_, premios) -> premios.sumOf { it.cantidad } }
        assertEquals(mapOf(JugadorId(0) to 76L, JugadorId(1) to 76L, JugadorId(2) to 151L), premios)
    }

    @Test fun empateHighAsignaRestoDesdeBoton() {
        val origen = mano(listOf(101L, 101L, 101L), setOf(2))
        val alto = ValorAlto(CategoriaAlta.PAREJA, listOf(10, 7, 5, 2))
        val reglas = object : ReglasVariante by TexasHoldem {
            override fun evaluar(propias: List<Carta>, comunitarias: List<Carta>) =
                ResultadoEvaluacion(alta = alto)
        }
        val premios = calculador.distribuir(origen, reglas).associate { it.jugadorId to it.cantidad }
        assertEquals(mapOf(JugadorId(0) to 151L, JugadorId(1) to 152L), premios)
    }
}
