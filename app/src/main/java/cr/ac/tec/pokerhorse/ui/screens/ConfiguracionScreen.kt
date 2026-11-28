package cr.ac.tec.pokerhorse.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cr.ac.tec.pokerhorse.core.domain.ConfiguracionPartida

@Composable
fun ConfiguracionScreen(procesando: Boolean, error: String?,
    iniciar: (ConfiguracionPartida, String) -> Unit, volver: () -> Unit) {
    var nombre by rememberSaveable { mutableStateOf("Jugador") }
    var jugadores by rememberSaveable { mutableStateOf(4) }
    var fichas by rememberSaveable { mutableStateOf("1000") }
    var manos by rememberSaveable { mutableStateOf("6") }
    var validacion by rememberSaveable { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Configurar partida", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") },
            singleLine = true, enabled = !procesando)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { jugadores-- }, enabled = jugadores > 2 && !procesando) { Text("−") }
            Text("$jugadores jugadores · ${jugadores - 1} bots")
            OutlinedButton(onClick = { jugadores++ }, enabled = jugadores < 8 && !procesando) { Text("+") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(fichas, { fichas = it }, Modifier.weight(1f),
                label = { Text("Fichas iniciales") }, singleLine = true, enabled = !procesando)
            OutlinedTextField(manos, { manos = it }, Modifier.weight(1f),
                label = { Text("Manos por variante") }, singleLine = true, enabled = !procesando)
        }
        val defaults = ConfiguracionPartida()
        Text("Límites: ${defaults.apuestaPequena} / ${defaults.apuestaGrande} · Ante ${defaults.ante} · Bring-in ${defaults.bringIn}")
        (validacion ?: error)?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = volver, enabled = !procesando) { Text("Volver") }
            Button(enabled = !procesando, onClick = {
                try {
                    require(nombre.isNotBlank()) { "Ingresá un nombre." }
                    val config = ConfiguracionPartida(cantidadJugadores = jugadores,
                        fichasIniciales = fichas.toLongOrNull() ?: throw IllegalArgumentException("Fichas inválidas"),
                        manosPorVariante = manos.toIntOrNull() ?: throw IllegalArgumentException("Manos inválidas"))
                    validacion = null
                    iniciar(config, nombre.trim())
                } catch (e: IllegalArgumentException) { validacion = e.message }
                  catch (e: IllegalStateException) { validacion = e.message }
            }) { Text("Comenzar") }
        }
    }
}
