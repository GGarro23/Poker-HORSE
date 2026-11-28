package cr.ac.tec.pokerhorse.application

import cr.ac.tec.pokerhorse.core.bots.BotBasico
import cr.ac.tec.pokerhorse.core.bots.EstrategiaDeBot
import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.engine.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Adaptador anfitrión local. El estado autoritativo nunca sale de esta clase. */
class SesionLocal private constructor(
    private val autoridad: SesionAutoritativa,
    private val humano: JugadorId,
    private val bots: Map<JugadorId, EstrategiaDeBot>,
    private val pausaBot: Long
) : SesionPartida {
    private val _vista = MutableStateFlow(autoridad.vista(humano))
    override val vista = _vista.asStateFlow()

    override suspend fun aplicar(revision: Long, accion: Accion): RespuestaComando =
        autoridad.aplicar(humano, revision, accion).also { publicar() }

    override suspend fun siguienteMano(revision: Long): RespuestaComando =
        autoridad.siguienteMano(humano, revision).also { publicar() }

    private fun publicar() { _vista.value = autoridad.vista(humano) }

    override suspend fun avanzarAutomaticos() {
        while (true) {
            val turno = _vista.value.vista.turno ?: return
            val bot = bots[turno] ?: return
            delay(pausaBot)
            val propia = autoridad.vista(turno)
            val accion = checkNotNull(bot.decidir(propia.vista)) { "Bot sin acción" }
            val respuesta = autoridad.aplicar(turno, propia.revision, accion)
            publicar()
            check(respuesta is RespuestaComando.Aceptado) { "Comando de bot rechazado: $respuesta" }
        }
    }

    companion object {
        fun crear(config: ConfiguracionPartida, nombre: String, pausaBot: Long = 300): SesionLocal {
            val humano = JugadorId(0)
            val motor = MotorDeJuego()
            val nombres = listOf(nombre) + (1 until config.cantidadJugadores).map { "Bot $it" }
            val autoridad = SesionAutoritativa(motor, motor.iniciar(config, nombres), humano)
            val bots = (1 until config.cantidadJugadores).associate { JugadorId(it) to BotBasico() }
            return SesionLocal(autoridad, humano, bots, pausaBot)
        }
    }
}
