package cr.ac.tec.pokerhorse.core.engine

import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.variants.ReglasVariante

internal class Repartidor {
    fun repartir(mano: EstadoMano, calle: Calle, reglas: ReglasVariante): EstadoMano {
        require(mano.variante == reglas.variante && calle in reglas.calles)
        require(mano.turno == null)
        val plan = reglas.planReparto(calle, mano)
        val jugadores = mano.obtenerJugadores()
        val participaciones = mano.obtenerParticipaciones().associateBy { it.jugadorId }.toMutableMap()
        val comunitarias = mano.obtenerComunitarias().toMutableList()
        val contendientes = jugadores.filter {
            participaciones[it.id]?.estado?.let { estado -> estado != EstadoJugador.RETIRADO } == true
        }.sortedBy { it.asiento.posicion }
        val orden = contendientes.filter { it.asiento.posicion > mano.boton.posicion } +
            contendientes.filter { it.asiento.posicion <= mano.boton.posicion }
        var mazo = mano.mazo
        fun extraer(): Carta {
            val resultado = mazo.extraer()
            mazo = resultado.mazoRestante
            return resultado.carta
        }
        check(mazo.cantidad >= plan.quemadas + plan.comunitarias +
            orden.size * (plan.ocultas + plan.visibles))
        repeat(plan.quemadas) { extraer() }
        repeat(plan.ocultas + plan.visibles) {
            for (jugador in orden) {
                participaciones[jugador.id] = participaciones.getValue(jugador.id).agregarCarta(extraer())
            }
        }
        repeat(plan.comunitarias) { comunitarias.add(extraer()) }
        return EstadoMano(mano.variante, calle, mazo, jugadores,
            mano.obtenerParticipaciones().map { participaciones.getValue(it.jugadorId) },
            comunitarias, null, mano.boton, mano.aumentosCalle)
    }
}
