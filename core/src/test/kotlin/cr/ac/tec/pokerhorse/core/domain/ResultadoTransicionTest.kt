package cr.ac.tec.pokerhorse.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ResultadoTransicionTest {

    @Test
    fun exitoContieneEstado() {
        val resultado: ResultadoTransicion<Long> =
            ResultadoTransicion.Exito(1000L)

        val exito = assertIs<ResultadoTransicion.Exito<Long>>(
            resultado
        )

        assertEquals(1000L, exito.estado)
    }

    @Test
    fun rechazoContieneError() {
        val resultado: ResultadoTransicion<Long> =
            ResultadoTransicion.Rechazo(
                ErrorDominio.FUERA_DE_TURNO
            )

        val rechazo = assertIs<ResultadoTransicion.Rechazo>(
            resultado
        )

        assertEquals(ErrorDominio.FUERA_DE_TURNO, rechazo.error)
    }

    @Test
    fun rechazoCompatibleConDiferentesTipos() {
        val rechazo = ResultadoTransicion.Rechazo(
            ErrorDominio.ACCION_NO_PERMITIDA
        )

        val resultadoMano: ResultadoTransicion<EstadoMano> =
            rechazo

        val resultadoPartida: ResultadoTransicion<EstadoPartida> =
            rechazo

        assertEquals(rechazo, resultadoMano)
        assertEquals(rechazo, resultadoPartida)
    }

    @Test
    fun resultadoPermiteWhenExhaustivo() {
        val resultado: ResultadoTransicion<Long> =
            ResultadoTransicion.Exito(500L)

        val valor = when (resultado) {
            is ResultadoTransicion.Exito -> resultado.estado
            is ResultadoTransicion.Rechazo -> -1L
        }

        assertEquals(500L, valor)
    }

    @Test
    fun exitosEquivalentes() {
        assertEquals(
            ResultadoTransicion.Exito(1000L),
            ResultadoTransicion.Exito(1000L)
        )
    }

    @Test
    fun rechazosEquivalentes() {
        assertEquals(
            ResultadoTransicion.Rechazo(
                ErrorDominio.MONTO_INVALIDO
            ),
            ResultadoTransicion.Rechazo(
                ErrorDominio.MONTO_INVALIDO
            )
        )
    }
}