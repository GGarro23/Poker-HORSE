package cr.ac.tec.pokerhorse.application

import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.engine.RespuestaComando
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class SesionLocalTest {
    @Test fun partidaCompletaUnaRotacion() = runBlocking {
        val sesion = SesionLocal.crear(ConfiguracionPartida(cantidadJugadores = 3,
            manosPorVariante = 1, fichasIniciales = 10000), "Humano", pausaBot = 0)
        val variantes = mutableListOf<Variante>()
        repeat(5) { indice ->
            sesion.avanzarAutomaticos()
            variantes.add(sesion.vista.value.vista.variante)
            var pasos = 0
            while (!sesion.vista.value.vista.finalizada) {
                check(++pasos < 100)
                val version = sesion.vista.value
                assertEquals(version.vista.jugadorId, version.vista.turno)
                val accion = listOf(Accion.Check, Accion.Call, Accion.Fold)
                    .first { it in version.vista.accionesLegales }
                assertTrue(sesion.aplicar(version.revision, accion) is RespuestaComando.Aceptado)
                sesion.avanzarAutomaticos()
            }
            val fin = sesion.vista.value
            assertEquals(0L, fin.vista.pozo)
            assertEquals(30000L, fin.vista.participantes.sumOf { it.fichas })
            assertFalse(fin.vista.premios.isEmpty())
            if (indice < 4) assertTrue(sesion.siguienteMano(fin.revision) is RespuestaComando.Aceptado)
        }
        assertEquals(listOf(Variante.HOLD_EM, Variante.OMAHA_HI_LO, Variante.RAZZ,
            Variante.STUD, Variante.STUD_HI_LO), variantes)
    }

    @Test fun revisionObsoletaNoCambiaMesa() = runBlocking {
        val sesion = SesionLocal.crear(ConfiguracionPartida(cantidadJugadores = 2), "Humano", 0)
        sesion.avanzarAutomaticos()
        val antes = sesion.vista.value
        assertEquals(RespuestaComando.RevisionObsoleta, sesion.aplicar(antes.revision - 1, Accion.Fold))
        assertEquals(antes, sesion.vista.value)
    }
}
