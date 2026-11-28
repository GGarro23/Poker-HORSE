package cr.ac.tec.pokerhorse.core.variants

import cr.ac.tec.pokerhorse.core.domain.*
import kotlin.test.*

class OrdenAccionTest {
    private val config = ConfiguracionPartida()

    private fun cartas(texto: String): List<Carta> = texto.split(" ").map {
        Carta(when (it[1]) {
            's' -> Palo.PICA
            'd' -> Palo.DIAMANTE
            'c' -> Palo.TREBOL
            'h' -> Palo.CORAZON
            else -> error("Palo desconocido")
        }, Rango.entries["23456789TJQKA".indexOf(it[0])])
    }

    private fun mano(
        variante: Variante, calle: Calle, propias: List<List<Carta>>,
        boton: Int = 0, estados: Map<Int, EstadoJugador> = emptyMap(),
        fichas: Map<Int, Long> = emptyMap()
    ): EstadoMano {
        val jugadores = propias.indices.map {
            Jugador(JugadorId(it), "Jugador $it", Asiento(it), fichas[it] ?: 1000L)
        }
        val participaciones = propias.mapIndexed { i, cartas ->
            ParticipacionMano(JugadorId(i), cartas, estado = estados[i] ?: EstadoJugador.ACTIVO)
        }
        var mazo = Mazo.crear()
        // Extraer las cartas de prueba del mazo sin crear duplicados.
        val usadas = propias.flatten().toSet()
        while (mazo.obtenerCartas().any { it in usadas }) {
            mazo = mazo.extraer().mazoRestante
        }
        return EstadoMano(variante, calle, mazo, jugadores, participaciones, boton = Asiento(boton))
    }

    private fun ids(vararg valores: Int) = valores.map { JugadorId(it) }

    @Test fun holdemHeadsUp() {
        val propias = List(2) { emptyList<Carta>() }
        val pre = mano(Variante.HOLD_EM, Calle.PREFLOP, propias)
        val flop = mano(Variante.HOLD_EM, Calle.FLOP, propias)
        assertEquals(ids(0, 1), TexasHoldem.ordenAccion(pre))
        assertEquals(ids(1, 0), TexasHoldem.ordenAccion(flop))
        assertEquals(listOf(ApuestaForzosa(JugadorId(0), 10L),
            ApuestaForzosa(JugadorId(1), 20L)), TexasHoldem.apuestasForzosas(pre, config))
    }

    @Test fun omahaConCuatroJugadores() {
        val propias = List(4) { emptyList<Carta>() }
        assertEquals(ids(3, 0, 1, 2), OmahaHiLo.ordenAccion(
            mano(Variante.OMAHA_HI_LO, Calle.PREFLOP, propias)))
        assertEquals(ids(1, 2, 3, 0), OmahaHiLo.ordenAccion(
            mano(Variante.OMAHA_HI_LO, Calle.FLOP, propias)))
    }

    @Test fun omitirRetiradosYAllIn() {
        val estado = mano(Variante.HOLD_EM, Calle.FLOP, List(4) { emptyList<Carta>() },
            estados = mapOf(1 to EstadoJugador.RETIRADO, 2 to EstadoJugador.ALL_IN),
            fichas = mapOf(2 to 0L))
        assertEquals(ids(3, 0), TexasHoldem.ordenAccion(estado))
    }

    private fun terceraStud() = listOf(cartas("As Kd 2h"), cartas("Ac Qd 2c"), cartas("Ah Jd 9s"))

    @Test fun studBringInPorPalo() {
        val estado = mano(Variante.STUD, Calle.TERCERA, terceraStud())
        assertEquals(ids(1, 2, 0), SevenCardStud.ordenAccion(estado))
        val forzosas = SevenCardStud.apuestasForzosas(estado, config)
        assertEquals(4, forzosas.size)
        assertTrue(forzosas.take(3).all { it.esAnte && it.cantidad == 2L })
        assertEquals(ApuestaForzosa(JugadorId(1), 5L), forzosas.last())
    }

    @Test fun razzAsBajoYDesempatePorPalo() {
        val estado = mano(Variante.RAZZ, Calle.TERCERA,
            listOf(cartas("2s 3d Ah"), cartas("4c 5d Ks"), cartas("6h 7d Kc")))
        assertEquals(ids(1, 2, 0), Razz.ordenAccion(estado))
        assertEquals(JugadorId(1), Razz.apuestasForzosas(estado, config).last().jugadorId)
    }

    @Test fun studHiLoUsaOrdenAlto() {
        val estado = mano(Variante.STUD_HI_LO, Calle.TERCERA,
            listOf(cartas("2s 3d Ah"), cartas("4c 5d 2h"), cartas("6h 7d Kc")))
        assertEquals(ids(1, 2, 0), StudEightOrBetter.ordenAccion(estado))
    }

    @Test fun empateDeManosVisiblesPorAsiento() {
        val estado = mano(Variante.STUD, Calle.CUARTA,
            listOf(cartas("2c 3d Kh Qs"), cartas("4c 5d Ks Qh"), cartas("6c 7d Jh Ts")))
        assertEquals(ids(1, 2, 0), SevenCardStud.ordenAccion(estado))
    }

    @Test fun studPriorizaParejaVisible() {
        val estado = mano(Variante.STUD, Calle.CUARTA,
            listOf(cartas("2c 3d Ah Ks"), cartas("4c 5d 2h 2s"), cartas("6c 7d Jh Ts")))
        assertEquals(JugadorId(1), SevenCardStud.ordenAccion(estado).first())
    }

    @Test fun razzEvitaParejasVisibles() {
        val estado = mano(Variante.RAZZ, Calle.CUARTA,
            listOf(cartas("2c 3d Ah As"), cartas("4c 5d 2h 3s"), cartas("6c 7d Jh Ts")))
        assertEquals(JugadorId(1), Razz.ordenAccion(estado).first())
    }

    @Test fun bringInSeDesplazaSiAnteAgotaFichas() {
        val estado = mano(Variante.STUD, Calle.TERCERA, terceraStud(), fichas = mapOf(1 to 2L))
        assertEquals(JugadorId(2), SevenCardStud.apuestasForzosas(estado, config).last().jugadorId)
    }

    @Test fun excepcionCuartaCalle() {
        val propias = listOf(cartas("2c 3d Ah As"), cartas("4c 5d 2h 3s"))
        for (variante in listOf(Variante.STUD, Variante.STUD_HI_LO, Variante.RAZZ)) {
            val reglas = Variantes.obtener(variante)
            val estado = mano(variante, Calle.CUARTA, propias)
            assertEquals(if (variante == Variante.STUD) setOf(20L, 40L) else setOf(20L),
                reglas.incrementosPermitidos(estado, config))
            assertEquals(3, reglas.maximoAumentosPorCalle)
        }
        val sinPareja = mano(Variante.STUD, Calle.CUARTA,
            listOf(cartas("2c 3d Ah Ks"), cartas("4c 5d 2h 3s")))
        assertEquals(setOf(20L), SevenCardStud.incrementosPermitidos(sinPareja, config))
    }

    @Test fun septimaCalleConOchoJugadores() {
        var mazo = Mazo.crear()
        val jugadores = List(8) { Jugador(JugadorId(it), "Jugador $it", Asiento(it), 1000L) }
        val participaciones = jugadores.map { jugador ->
            val propias = List(6) {
                val extraccion = mazo.extraer()
                mazo = extraccion.mazoRestante
                extraccion.carta
            }
            ParticipacionMano(jugador.id, propias)
        }
        repeat(3) { mazo = mazo.extraer().mazoRestante }
        assertEquals(1, mazo.cantidad)
        for (variante in listOf(Variante.STUD, Variante.RAZZ, Variante.STUD_HI_LO)) {
            val estado = EstadoMano(variante, Calle.SEPTIMA, mazo, jugadores, participaciones,
                boton = Asiento(0))
            assertEquals(PlanReparto(comunitarias = 1), Variantes.obtener(variante)
                .planReparto(Calle.SEPTIMA, estado))
        }
    }

    @Test fun evaluacionConSeptimaCompartida() {
        val propias = cartas("As 2d 3c 4h Kd Qc")
        val comunitaria = cartas("5s")
        val resultado = StudEightOrBetter.evaluar(propias, comunitaria)
        assertEquals(listOf(5, 4, 3, 2, 1), resultado.baja?.desempates)
        assertFailsWith<IllegalArgumentException> {
            SevenCardStud.evaluar(propias, cartas("As"))
        }
    }
}
