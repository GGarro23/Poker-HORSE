package cr.ac.tec.pokerhorse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cr.ac.tec.pokerhorse.application.*
import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.engine.RespuestaComando
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class PartidaViewModel : ViewModel() {
    private var sesion: SesionPartida? = null
    private var observacion: Job? = null
    private var operacion: Job? = null
    private val _estado = MutableStateFlow(PartidaUiState())
    val estado = _estado.asStateFlow()

    fun configurar() { _estado.value = PartidaUiState(Pantalla.CONFIGURACION) }

    fun iniciar(config: ConfiguracionPartida, nombre: String) = ejecutar {
        val local = withContext(Dispatchers.Default) { SesionLocal.crear(config, nombre) }
        sesion = local
        _estado.value = PartidaUiState(Pantalla.MESA, local.vista.value, true)
        observacion?.cancel()
        observacion = viewModelScope.launch {
            local.vista.collect { vista -> _estado.update { it.copy(mesa = vista) } }
        }
        withContext(Dispatchers.Default) { local.avanzarAutomaticos() }
    }

    fun actuar(revision: Long, accion: Accion) = ejecutar {
        val actual = sesion ?: return@ejecutar
        val respuesta = withContext(Dispatchers.Default) { actual.aplicar(revision, accion) }
        if (procesar(respuesta)) withContext(Dispatchers.Default) { actual.avanzarAutomaticos() }
    }

    fun siguiente(revision: Long) = ejecutar {
        val actual = sesion ?: return@ejecutar
        val respuesta = withContext(Dispatchers.Default) { actual.siguienteMano(revision) }
        if (procesar(respuesta)) withContext(Dispatchers.Default) { actual.avanzarAutomaticos() }
    }

    private fun procesar(respuesta: RespuestaComando): Boolean {
        _estado.update { it.copy(mesa = sesion?.vista?.value) }
        return when (respuesta) {
            is RespuestaComando.Aceptado -> true
            is RespuestaComando.Rechazado -> {
                _estado.update {
                    if (respuesta.error == ErrorDominio.PARTIDA_FINALIZADA)
                        it.copy(pantalla = Pantalla.FIN)
                    else it.copy(error = respuesta.error.name)
                }
                false
            }
            RespuestaComando.RevisionObsoleta -> {
                _estado.update { it.copy(error = "La mesa cambió. Intentá de nuevo.") }
                false
            }
        }
    }

    fun reintentarBots() = ejecutar {
        val actual = sesion ?: return@ejecutar
        withContext(Dispatchers.Default) { actual.avanzarAutomaticos() }
    }

    fun menu() {
        if (_estado.value.procesando) return
        observacion?.cancel()
        sesion = null
        _estado.value = PartidaUiState()
    }

    private fun ejecutar(bloque: suspend () -> Unit) {
        if (_estado.value.procesando) return
        _estado.update { it.copy(procesando = true, error = null) }
        operacion = viewModelScope.launch {
            try { bloque() }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) { _estado.update { it.copy(error = e.message ?: "Error inesperado") } }
            finally { _estado.update { it.copy(procesando = false, mesa = sesion?.vista?.value) } }
        }
    }
}
