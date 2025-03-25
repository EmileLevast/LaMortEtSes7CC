package affichageMobile

import CHAR_SEP_EQUIPEMENT
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import configuration.GraphicConstantsFullGrid
import configuration.getGraphicConstants
import org.koin.compose.koinInject

@Composable
fun layoutDetailJoueur(infoToShow: String, onSave: (String) -> Unit) {

    val graphicsConsts = getGraphicConstants()

    var isShowingAddDetailPopup by remember { mutableStateOf(false) }
    var isShowingModifyDetailPopup by remember { mutableStateOf<String?>(null) }

    if (isShowingAddDetailPopup) {
        AlertDialogAjoutDetail({
            onSave((infoToShow + CHAR_SEP_EQUIPEMENT + it).removeSurrounding(CHAR_SEP_EQUIPEMENT))
        }, { isShowingAddDetailPopup = false })
    } else if (isShowingModifyDetailPopup != null) {
        val strToModify = isShowingModifyDetailPopup.toString()
        AlertDialogAjoutDetail({
            onSave(infoToShow.replace(strToModify, it).removeSurrounding(CHAR_SEP_EQUIPEMENT))
        }, { isShowingModifyDetailPopup = null }, strToModify)
    }

    Column(Modifier.fillMaxWidth()) {

        infoToShow.split(CHAR_SEP_EQUIPEMENT).forEach {
            if (it.isNotBlank()) {
                Row(
                    Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(Modifier.weight(1f).fillMaxHeight().padding(2.dp)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                                .background(MaterialTheme.colorScheme.secondary)
                        ) {
                            Text(
                                it,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSecondary,
                                style = TextStyle(fontFamily = FontFamily.Default),
                            )
                        }

                    }
                    IconButton(onClick = {
                        val newInfosWithDeleted = infoToShow.replace(it, "")
                            .replace(
                                "$CHAR_SEP_EQUIPEMENT${CHAR_SEP_EQUIPEMENT}",
                                CHAR_SEP_EQUIPEMENT
                            )//on supprime l'ancien detail
                        onSave(newInfosWithDeleted.removeSurrounding(CHAR_SEP_EQUIPEMENT))
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
    initialContent: String? = null
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
                label = { Text("nouveau detail") },
                textStyle = TextStyle(fontFamily = FontFamily.Default)
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