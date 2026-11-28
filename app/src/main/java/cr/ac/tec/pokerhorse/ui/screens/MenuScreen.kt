package cr.ac.tec.pokerhorse.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cr.ac.tec.pokerhorse.ui.theme.Dorado

@Composable
fun MenuScreen(jugar: () -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("♠  ♥  ♣  ♦", style = MaterialTheme.typography.headlineMedium, color = Dorado)
        Text("POKER HORSE", style = MaterialTheme.typography.displaySmall, color = Dorado)
        Text("H · O · R · S · E", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(24.dp))
        Button(onClick = jugar, modifier = Modifier.heightIn(min = 48.dp)) { Text("Partida local") }
        Text("Un jugador · Bots · Fichas virtuales")
    }
}
