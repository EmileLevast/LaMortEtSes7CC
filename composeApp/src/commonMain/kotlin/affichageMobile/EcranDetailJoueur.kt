package affichageMobile

import CHAR_SEP_EQUIPEMENT
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField

import Joueur
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.MaterialTheme
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
import configuration.GraphicConstantsFullGrid
import org.koin.compose.koinInject

@Composable
fun layoutDetailJoueur(infoToShow: String, onSave: (String) -> Unit) {

    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()

    var isShowingAddDetailPopup by remember { mutableStateOf(false) }
    var isShowingModifyDetailPopup by remember { mutableStateOf<String?>(null) }

    if (isShowingAddDetailPopup) {
        AlertDialogAjoutDetail({
            onSave(infoToShow + CHAR_SEP_EQUIPEMENT + it)
        }, { isShowingAddDetailPopup = false })
    }else if(isShowingModifyDetailPopup != null){
        val strToModify = isShowingModifyDetailPopup.toString()
        AlertDialogAjoutDetail( {
            infoToShow.replace(strToModify,it)
        }, { isShowingModifyDetailPopup = null }, strToModify)
    }

    Column(Modifier.fillMaxWidth()) {

        infoToShow.split(CHAR_SEP_EQUIPEMENT).forEach {
            if (it.isNotBlank()) {
                Row(Modifier.fillMaxWidth()) {
                    Card(Modifier.weight(1f).padding(2.dp)) {
                        Text(
                            it,
                            Modifier.align(Alignment.CenterHorizontally)
                                .background(MaterialTheme.colorScheme.secondary).fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSecondary,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    IconButton(onClick = {
                        val newInfosWithDeleted = infoToShow.replace(it, "")
                            .replace("$CHAR_SEP_EQUIPEMENT${CHAR_SEP_EQUIPEMENT}",
                                CHAR_SEP_EQUIPEMENT
                            )//on supprime l'ancien detail
                        onSave(newInfosWithDeleted)
                    })
                    {
                        Icon(Icons.Rounded.Delete, "supprimer detail")
                    }
                    IconButton(onClick = {
                        //on ouvre la pop de modication
                        isShowingModifyDetailPopup = it
                    })
                    {
                        Icon(Icons.Rounded.Edit, "editer detail")
                    }
                }
            }
        }
        Card(
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(3.dp),
            border = BorderStroke(graphicsConsts.widthBorder, Color.LightGray),
            shape = RoundedCornerShape(50)
        ) {
            IconButton(onClick = {
                isShowingAddDetailPopup = true
            })
            {
                Icon(Icons.Rounded.Add, "ajouter detail")
            }
        }

    }

}


@Composable
fun AlertDialogAjoutDetail(
    onAddingDetail: (String) -> Unit,
    onDismissRequest: () -> Unit,
    initialContent:String? = null
) {

    var detailActuel by remember { mutableStateOf(initialContent ?: "") }

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