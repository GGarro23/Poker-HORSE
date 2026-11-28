package cr.ac.tec.pokerhorse.core.betting

import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.engine.*

internal class GestorApuestas {
    private fun objetivos(mano: EstadoMano, ronda: RondaApuestas, id: JugadorId): List<Long> {
        if (id !in ronda.derechoSubida || mano.activos().size < 2) return emptyList()
        if (ronda.abierta && mano.aumentosCalle >= ronda.maximoAumentos) return emptyList()
        return ronda.incrementos.sorted().mapNotNull { incremento ->
            if (!ronda.abierta) incremento.takeIf { it > ronda.objetivo }
            else if (incremento <= Long.MAX_VALUE - ronda.objetivo) ronda.objetivo + incremento
            else null
        }
    }

    fun legales(mano: EstadoMano, ronda: RondaApuestas): List<Accion> {
        val id = mano.turno ?: return emptyList()
        val jugador = mano.buscarJugador(id)!!
        val participacion = mano.buscarParticipacion(id)!!
        val aporte = participacion.aporteCalle
        val deuda = (ronda.objetivo - aporte).coerceAtLeast(0L)
        val acciones = mutableListOf<Accion>(Accion.Fold)
        acciones.add(if (deuda == 0L) Accion.Check else Accion.Call)
        val objetivos = objetivos(mano, ronda, id)
        for (objetivo in objetivos) {
            val monto = objetivo - aporte
            if (monto <= jugador.fichas && monto > 0) {
                acciones.add(if (ronda.objetivo == 0L) Accion.Bet(monto) else Accion.Raise(objetivo))
            }
        }
        // All-in es una llamada corta o una apuesta/subida que no supera
        // el menor objetivo completo disponible (o coincide con uno legal).
        val maximo = aporte + jugador.fichas
        if (jugador.fichas > 0 && (jugador.fichas <= deuda ||
                objetivos.any { it == maximo } ||
                (objetivos.isNotEmpty() && maximo > ronda.objetivo && maximo < objetivos.min()))) {
            acciones.add(Accion.AllIn)
        }
        return acciones.toList()
    }

    fun aplicar(mano: EstadoMano, ronda: RondaApuestas, accion: Accion): Pair<EstadoMano, RondaApuestas> {
        require(accion in legales(mano, ronda))
        val id = mano.turno!!
        val jugador = mano.buscarJugador(id)!!
        val participacion = mano.buscarParticipacion(id)!!
        val monto = when (accion) {
            Accion.Fold, Accion.Check -> 0L
            Accion.Call -> minOf(jugador.fichas, (ronda.objetivo - participacion.aporteCalle).coerceAtLeast(0L))
            Accion.AllIn -> jugador.fichas
            is Accion.Bet -> accion.cantidad
            is Accion.Raise -> accion.hasta - participacion.aporteCalle
        }
        val aporte = if (accion == Accion.Fold) ResultadoAporte(jugador,
            participacion.cambiarEstado(EstadoJugador.RETIRADO))
        else participacion.aportarFichas(jugador, monto)

        val nuevoAporte = aporte.participacion.aporteCalle
        val completo = nuevoAporte > ronda.objetivo && nuevoAporte in objetivos(mano, ronda, id)
        val objetivo = maxOf(ronda.objetivo, nuevoAporte)
        val aumentos = mano.aumentosCalle + if (completo && ronda.abierta) 1 else 0
        val actualizado = mano.reconstruir(
            jugadores = mano.obtenerJugadores().map { if (it.id == id) aporte.jugador else it },
            participaciones = mano.obtenerParticipaciones().map { if (it.jugadorId == id) aporte.participacion else it },
            turno = null, aumentos = aumentos)
        val activos = actualizado.activos()
        val pendientes = if (completo) activos - id else
            ((ronda.pendientes - id) + activos.filter {
                actualizado.buscarParticipacion(it)!!.aporteCalle < objetivo
            }).intersect(activos) - id
        val derechos = if (completo) activos - id else (if (accion == Accion.Check) ronda.derechoSubida else ronda.derechoSubida - id).intersect(activos)
        val incrementos = if (completo && ronda.incrementos.size > 1) {
            val elegido = if (ronda.abierta) nuevoAporte - ronda.objetivo else nuevoAporte
            if (elegido == ronda.incrementos.max()) setOf(elegido) else ronda.incrementos
        } else ronda.incrementos
        return actualizado to ronda.copy(pendientes = pendientes, derechoSubida = derechos,
            objetivo = objetivo, incrementos = incrementos, abierta = ronda.abierta || completo)
    }
}
