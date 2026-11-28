package cr.ac.tec.pokerhorse.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cr.ac.tec.pokerhorse.application.Pantalla
import cr.ac.tec.pokerhorse.ui.screens.*
import cr.ac.tec.pokerhorse.viewmodel.PartidaViewModel

@Composable
fun PokerApp(vm: PartidaViewModel = viewModel()) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    var confirmar by remember { mutableStateOf(false) }
    BackHandler(estado.pantalla != Pantalla.MENU) {
        if (estado.pantalla == Pantalla.MESA) confirmar = true else vm.menu()
    }
    Surface(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().safeDrawingPadding().padding(12.dp)) {
            when (estado.pantalla) {
                Pantalla.MENU -> MenuScreen(vm::configurar)
                Pantalla.CONFIGURACION -> ConfiguracionScreen(
                    estado.procesando, estado.error, vm::iniciar, vm::menu
                )
                Pantalla.MESA -> estado.mesa?.let {
                    MesaScreen(it, estado.procesando, estado.error, vm::actuar,
                        vm::siguiente, vm::reintentarBots, { confirmar = true })
                }
                Pantalla.FIN -> estado.mesa?.let { FinScreen(it.vista, vm::menu) }
            }
        }
    }
    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text("¿Salir de la partida?") },
            text = { Text("Se perderá la partida local actual.") },
            confirmButton = {
                TextButton(enabled = !estado.procesando, onClick = {
                    confirmar = false
                    vm.menu()
                }) { Text("Salir") }
            },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Continuar") } }
        )
    }
}
