package cr.ac.tec.pokerhorse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.core.engine.VistaJugador
import cr.ac.tec.pokerhorse.core.engine.VistaVersionada
import cr.ac.tec.pokerhorse.ui.components.*
import cr.ac.tec.pokerhorse.ui.theme.*

private fun Variante.titulo() = when (this) {
    Variante.HOLD_EM -> "H · Texas Hold'em"
    Variante.OMAHA_HI_LO -> "O · Omaha Hi-Lo"
    Variante.RAZZ -> "R · Razz"
    Variante.STUD -> "S · Seven Card Stud"
    Variante.STUD_HI_LO -> "E · Stud Eight-or-Better"
}

@Composable
fun MesaScreen(version: VistaVersionada, ocupado: Boolean, error: String?,
    actuar: (Long, Accion) -> Unit, siguiente: (Long) -> Unit,
    reintentar: () -> Unit, salir: () -> Unit) {
    val vista = version.vista
    BoxWithConstraints(Modifier.fillMaxSize().background(Mesa).padding(8.dp)) {
        val ancho = maxWidth >= 650.dp
        if (ancho) {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState())) {
                    MesaContenido(vista, columnas = 3)
                }
                Column(Modifier.width(220.dp).fillMaxHeight().verticalScroll(rememberScrollState())) {
                    PanelAcciones(version, ocupado, error, actuar, siguiente, reintentar, salir)
                }
            }
        } else {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MesaContenido(vista, columnas = 2)
                PanelAcciones(version, ocupado, error, actuar, siguiente, reintentar, salir)
            }
        }
    }
}

@Composable
private fun MesaContenido(vista: VistaJugador, columnas: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(vista.variante.titulo(), color = Dorado, style = MaterialTheme.typography.titleLarge)
        Text("${vista.calle.name} · Manos completadas: ${vista.manosCompletadas}")
        Text("Pozo: ${vista.pozo}", style = MaterialTheme.typography.headlineSmall, color = Dorado)
        Text("Turno: " + (vista.participantes.firstOrNull { it.id == vista.turno }?.nombre ?: "Mano concluida"))
        if (vista.comunitarias.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                vista.comunitarias.forEach { CartaVisual(it) }
            }
        }
        val rivales = vista.participantes.filter { it.id != vista.jugadorId }
        rivales.chunked(columnas).forEach { grupo ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                grupo.forEach { JugadorVisual(it, vista, Modifier.weight(1f)) }
                repeat(columnas - grupo.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        vista.participantes.firstOrNull { it.id == vista.jugadorId }?.let {
            JugadorVisual(it, vista, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun PanelAcciones(version: VistaVersionada, ocupado: Boolean, error: String?,
    actuar: (Long, Accion) -> Unit, siguiente: (Long) -> Unit,
    reintentar: () -> Unit, salir: () -> Unit) {
    val vista = version.vista
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (vista.finalizada) {
            Text("Resultado de la mano", style = MaterialTheme.typography.titleLarge, color = Dorado)
            vista.premios.groupBy { it.jugadorId }.forEach { (id, premios) ->
                val nombre = vista.participantes.firstOrNull { it.id == id }?.nombre ?: id.identificador.toString()
                Text("$nombre recibe ${premios.sumOf { it.cantidad }} fichas")
            }
            Button(onClick = { siguiente(version.revision) }, enabled = !ocupado,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text("Continuar") }
        } else {
            Text(if (vista.turno == vista.jugadorId) "Tu turno" else "Turno de bot",
                style = MaterialTheme.typography.titleMedium)
            vista.accionesLegales.forEach { accion ->
                Button(onClick = { actuar(version.revision, accion) },
                    enabled = !ocupado && vista.turno == vista.jugadorId,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Text(etiqueta(accion, vista.montosAcciones[accion]))
                }
            }
        }
        if (ocupado) LinearProgressIndicator(Modifier.fillMaxWidth())
        error?.let {
            Text(it, color = Marfil)
            if (!vista.finalizada && vista.turno != vista.jugadorId)
                OutlinedButton(onClick = reintentar, enabled = !ocupado) { Text("Reintentar") }
        }
        OutlinedButton(onClick = salir, enabled = !ocupado,
            modifier = Modifier.fillMaxWidth()) { Text("Salir") }
    }
}

private fun etiqueta(accion: Accion, monto: Long?) = when (accion) {
    Accion.Fold -> "Retirarse"
    Accion.Check -> "Pasar"
    Accion.Call -> "Igualar" + (monto?.let { " $it" } ?: "")
    Accion.AllIn -> "All-in" + (monto?.let { " $it" } ?: "")
    is Accion.Bet -> "Apostar ${accion.cantidad}"
    is Accion.Raise -> "Subir a ${accion.hasta}" + (monto?.let { " (+$it)" } ?: "")
}

@Composable
fun FinScreen(vista: VistaJugador, menu: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Fin de partida", style = MaterialTheme.typography.headlineLarge, color = Dorado)
        vista.participantes.sortedByDescending { it.fichas }.forEach {
            Text("${it.nombre} · ${it.fichas} fichas")
        }
        Button(onClick = menu) { Text("Menú principal") }
    }
}
