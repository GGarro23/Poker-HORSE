package cr.ac.tec.pokerhorse.application

import cr.ac.tec.pokerhorse.core.domain.Accion
import cr.ac.tec.pokerhorse.core.engine.RespuestaComando
import cr.ac.tec.pokerhorse.core.engine.VistaVersionada
import kotlinx.coroutines.flow.StateFlow

/** Puerto de cliente; la futura sesión LAN publicará las vistas del anfitrión aquí. */
interface SesionPartida {
    val vista: StateFlow<VistaVersionada>
    suspend fun aplicar(revision: Long, accion: Accion): RespuestaComando
    suspend fun siguienteMano(revision: Long): RespuestaComando
    suspend fun avanzarAutomaticos()
}
