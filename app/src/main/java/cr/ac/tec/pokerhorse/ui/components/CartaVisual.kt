package cr.ac.tec.pokerhorse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cr.ac.tec.pokerhorse.core.domain.*
import cr.ac.tec.pokerhorse.ui.theme.*

/** Único punto de sustitución de cartas por sprites pixel art. */
@Composable
fun CartaVisual(carta: Carta?) {
    val rango = carta?.rango?.let { when (it) {
        Rango.AS -> "A"; Rango.REY -> "K"; Rango.REINA -> "Q"; Rango.JOTA -> "J"
        else -> it.valor.toString()
    } }
    val palo = when (carta?.palo) {
        Palo.PICA -> "♠"; Palo.CORAZON -> "♥"; Palo.TREBOL -> "♣"; Palo.DIAMANTE -> "♦"
        null -> "◆"
    }
    val color = if (carta == null) Dorado else if (carta.palo in listOf(Palo.CORAZON, Palo.DIAMANTE)) Rojo else Color(0xFF172C25)
    val forma = RoundedCornerShape(3.dp)
    Box(Modifier.size(30.dp, 43.dp).background(if (carta == null) Madera else Marfil, forma)
        .border(1.dp, Dorado, forma).semantics {
            contentDescription = carta?.let { "${it.rango.name} de ${it.palo.name}" } ?: "Carta oculta"
        }, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (rango != null) Text(rango, color = color, fontSize = 12.sp, lineHeight = 13.sp)
            Text(palo, color = color, fontSize = 15.sp, lineHeight = 16.sp)
        }
    }
}
