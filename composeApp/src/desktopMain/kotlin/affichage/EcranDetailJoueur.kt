package affichage

import Joueur
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun layoutDetailJoueur(actuelJoueur:Joueur){

    var detailJoueur by remember {  mutableStateOf(actuelJoueur.details) }

    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()

    var isShowingAddDetailPopup by remember { mutableStateOf(false) }


    if(isShowingAddDetailPopup){
        AlertDialogAjoutDetail({detailJoueur+="\n" + it}, {isShowingAddDetailPopup=false})
    }

    Column(Modifier.fillMaxWidth()) {
        detailJoueur.split("\n").forEach {
            if(it.isNotBlank()){
                Card(Modifier.fillMaxWidth().padding(3.dp),border = BorderStroke(graphicsConsts.widthBorder, graphicsConsts.brushSpecialBorder), elevation = graphicsConsts.cardElevation) {
                    Text(it, Modifier.fillMaxWidth().padding(5.dp), textAlign = TextAlign.Center)
                }
            }
        }
        Card(Modifier.fillMaxWidth(0.4f).align(Alignment.CenterHorizontally).padding(3.dp),border = BorderStroke(graphicsConsts.widthBorder, Color.LightGray), elevation = graphicsConsts.cardElevation, shape = RoundedCornerShape(50)) {
            IconButton(onClick = {
                isShowingAddDetailPopup=true
            })
            {
                Icon(Icons.Rounded.Add, "ajouter detail", tint = Color.LightGray)
            }
        }
    }

}


@Composable
fun AlertDialogAjoutDetail(
    onAddingDetail : (String)->Unit,
    onDismissRequest: ()->Unit
) {

    var detailActuel by remember { mutableStateOf("detail actuel")}

    AlertDialog(
        title = {
            Text(text = "Ajouter détail")
        },
        text = {
            TextField(
                value = detailActuel,
                onValueChange = { detailActuel = it },
                label = { Text("nouveau detail") }
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onAddingDetail(detailActuel)
                    onDismissRequest()
                }
            ) {
                Text("Confirmer")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Annuler")
            }
        }
    )
}