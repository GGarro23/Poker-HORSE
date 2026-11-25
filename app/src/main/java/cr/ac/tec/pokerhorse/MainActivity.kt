package cr.ac.tec.pokerhorse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import cr.ac.tec.pokerhorse.core.CoreInfo
import cr.ac.tec.pokerhorse.ui.theme.PokerHORSETheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PokerHORSETheme {
                PantallaInicial(
                    mensaje = CoreInfo.mensaje()
                )
            }
        }
    }
}

@Composable
fun PantallaInicial(mensaje: String) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF172C25)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "♠ ♥ ♣ ♦",
                color = Color(0xFFE4BB70),
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "POKER HORSE",
                color = Color(0xFFE4BB70),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = mensaje,
                color = Color(0xFFF5E7CE),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "v0.1.0 - Development Build",
                color = Color(0xFF9BAEA4),
                fontSize = 12.sp
            )
        }
    }
}
