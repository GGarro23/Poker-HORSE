package cr.ac.tec.pokerhorse.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val Retro = darkColorScheme(
    primary = Dorado, onPrimary = Fondo,
    secondary = Marfil, onSecondary = Fondo,
    background = Fondo, onBackground = Marfil,
    surface = Fondo, onSurface = Marfil,
    surfaceVariant = Mesa, onSurfaceVariant = Marfil,
    error = Rojo, onError = Marfil
)

@Composable
fun PokerHORSETheme(content: @Composable () -> Unit) {
    val rectangular = RoundedCornerShape(3.dp)
    MaterialTheme(
        colorScheme = Retro, typography = Typography,
        shapes = Shapes(rectangular, rectangular, rectangular, rectangular, rectangular),
        content = content
    )
}
