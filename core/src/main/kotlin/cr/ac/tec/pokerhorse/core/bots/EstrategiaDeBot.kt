package cr.ac.tec.pokerhorse.core.bots

import cr.ac.tec.pokerhorse.core.domain.Accion
import cr.ac.tec.pokerhorse.core.engine.VistaJugador

fun interface EstrategiaDeBot {
    fun decidir(vista: VistaJugador): Accion?
}

/** Bot conservador, sin acceso al mazo ni a las cartas ocultas ajenas. */
class BotBasico : EstrategiaDeBot {
    override fun decidir(vista: VistaJugador): Accion? =
        listOf(Accion.Check, Accion.Call, Accion.Fold)
            .firstOrNull { it in vista.accionesLegales }
}
