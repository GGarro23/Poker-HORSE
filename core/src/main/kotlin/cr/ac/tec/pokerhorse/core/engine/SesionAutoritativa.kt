package cr.ac.tec.pokerhorse.core.engine

import cr.ac.tec.pokerhorse.core.domain.*

data class VistaVersionada(val revision: Long, val vista: VistaJugador)

sealed interface RespuestaComando {
    data class Aceptado(val revision: Long) : RespuestaComando
    data class Rechazado(val error: ErrorDominio) : RespuestaComando
    data object RevisionObsoleta : RespuestaComando
}

/**
 * Usar exclusivamente en el anfitrión. La capa de red identifica a los
 * jugadores por conexión autenticada, nunca por un ID declarado en el mensaje.
 * Las respuestas a clientes no contienen el estado completo.
 */
class SesionAutoritativa(private val motor: MotorDeJuego, inicial: EstadoMotor,
                         private val anfitrion: JugadorId) {
    private var estado = inicial
    private var revision = 0L

    init { require(inicial.mano.buscarJugador(anfitrion) != null) }

    @Synchronized
    fun vista(jugadorAutenticado: JugadorId): VistaVersionada =
        VistaVersionada(revision, motor.vistaPara(estado, jugadorAutenticado))

    @Synchronized
    fun aplicar(jugadorAutenticado: JugadorId, revisionEsperada: Long,
                accion: Accion): RespuestaComando {
        if (revisionEsperada != revision) return RespuestaComando.RevisionObsoleta
        return aceptar(motor.aplicar(estado, jugadorAutenticado, accion))
    }

    @Synchronized
    fun siguienteMano(jugadorAutenticado: JugadorId, revisionEsperada: Long): RespuestaComando {
        if (revisionEsperada != revision) return RespuestaComando.RevisionObsoleta
        if (jugadorAutenticado != anfitrion)
            return RespuestaComando.Rechazado(ErrorDominio.ACCION_NO_PERMITIDA)
        return aceptar(motor.siguienteMano(estado))
    }

    private fun aceptar(resultado: ResultadoTransicion<EstadoMotor>): RespuestaComando =
        when (resultado) {
            is ResultadoTransicion.Rechazo -> RespuestaComando.Rechazado(resultado.error)
            is ResultadoTransicion.Exito -> {
                revision = Math.addExact(revision, 1L)
                estado = resultado.estado
                RespuestaComando.Aceptado(revision)
            }
        }
}
