package affichage

import Joueur
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
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

    Column(Modifier.fillMaxWidth()) {
        actuelJoueur.details.split("\n").forEach {
            if(it.isNotBlank()){
                Card(Modifier.fillMaxWidth().padding(3.dp),border = BorderStroke(graphicsConsts.widthBorder, graphicsConsts.brushSpecialBorder), elevation = graphicsConsts.cardElevation) {
                    Text(it, Modifier.fillMaxWidth().padding(5.dp), textAlign = TextAlign.Center)
                }
            }
        }
        Card(Modifier.fillMaxWidth(0.4f).align(Alignment.CenterHorizontally).padding(3.dp),border = BorderStroke(graphicsConsts.widthBorder, Color.LightGray), elevation = graphicsConsts.cardElevation, shape = RoundedCornerShape(50)) {
            Icon(Icons.Rounded.Add, "ajouter detail", tint = Color.LightGray)
        }
    }
}