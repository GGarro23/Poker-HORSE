package cr.ac.tec.pokerhorse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cr.ac.tec.pokerhorse.core.domain.EstadoJugador
import cr.ac.tec.pokerhorse.core.engine.VistaJugador
import cr.ac.tec.pokerhorse.core.engine.VistaParticipante
import cr.ac.tec.pokerhorse.ui.theme.*

@Composable
fun JugadorVisual(jugador: VistaParticipante, vista: VistaJugador, modifier: Modifier = Modifier) {
    val propio = jugador.id == vista.jugadorId
    val turno = jugador.id == vista.turno
    Column(modifier.background(Fondo).border(if (turno) 2.dp else 1.dp,
        if (turno) Dorado else Madera).padding(6.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(buildString {
            if (turno) append("▶ ")
            append(jugador.nombre)
            if (propio) append(" (vos)")
            if (jugador.asiento == vista.boton) append(" · D")
        }, color = if (turno) Dorado else Marfil, style = MaterialTheme.typography.titleSmall)
        Text("Fichas: ${jugador.fichas} · Aporte: ${jugador.aporteCalle}",
            style = MaterialTheme.typography.bodySmall)
        Text("Total: ${jugador.aporteTotal} · " + when (jugador.estado) {
            EstadoJugador.ACTIVO -> "Activo"
            EstadoJugador.RETIRADO -> "Retirado"
            EstadoJugador.ALL_IN -> "All-in"
            null -> "Fuera de mano"
        }, style = MaterialTheme.typography.bodySmall)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            if (propio) vista.propias.forEach { CartaVisual(it) }
            else {
                repeat(jugador.cantidadCartasOcultas) { CartaVisual(null) }
                jugador.cartasVisibles.forEach { CartaVisual(it) }
            }
        }
    }
}
