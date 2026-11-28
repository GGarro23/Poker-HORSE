package cr.ac.tec.pokerhorse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cr.ac.tec.pokerhorse.ui.PokerApp
import cr.ac.tec.pokerhorse.ui.theme.PokerHORSETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { PokerHORSETheme { PokerApp() } }
    }
}
