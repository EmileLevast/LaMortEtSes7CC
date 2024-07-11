package affichage

import Joueur
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun layoutDetailJoueur(actuelJoueur:Joueur){

    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()

    Column {
        actuelJoueur.details.split("\n").forEach {
            Card(Modifier.fillMaxWidth().padding(3.dp),border = BorderStroke(graphicsConsts.widthBorder, graphicsConsts.brushSpecialBorder), elevation = graphicsConsts.cardElevation) {
                Text(it, Modifier.fillMaxWidth().padding(5.dp), textAlign = TextAlign.Center)
            }
        }
    }
}