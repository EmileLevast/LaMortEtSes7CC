package affichageMobile

import Equipe
import IMAGENAME_CARD_BACKGROUND
import Joueur
import affichage.AlertDialogChangeIp
import affichage.LayoutDrawerMenu
import affichage.buttonDarkStyled
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import configuration.GraphicConstantsFullGrid
import configuration.IConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.HeadBodyShowable
import network.ApiApp
import org.koin.compose.koinInject

@Composable
fun EcranPrincipal() {
    val apiApp = koinInject<ApiApp>()
    val config = koinInject<IConfiguration>()

    val coroutineScope = rememberCoroutineScope()
    val (equipes, setEquipes) = remember { mutableStateOf<List<Equipe>>(emptyList()) }
    val (triggerEquipe, setTriggerEquipe) = remember { mutableStateOf(false) }
    val (selectEquipe, setSelectEquipe) = remember { mutableStateOf<Equipe?>(null) }

    val (bitmapBackground, updateBitmapBackground) = remember { mutableStateOf<ImageBitmap?>(null) }


    //Variables de sélection du Joueur actuel
    var selectedJoueur: Joueur? by remember { mutableStateOf(null) }
    var nameSavedUser: String? by remember { mutableStateOf(config.getUserName()) }


    //MENU
    var openChangeIpDialog by remember { mutableStateOf(false) }
    val onCloseChangeIpDialog: () -> Unit = { openChangeIpDialog = false }

    LaunchedEffect(triggerEquipe) {
        coroutineScope.launch {
            setEquipes(withContext(Dispatchers.Default) {//dans un thread à part on maj toute l'equipe
                apiApp.searchEquipe(".*") ?: listOf()
            })

            updateBitmapBackground(withContext(Dispatchers.Default) {//dans un thread à part on recherche l'image background
                apiApp.downloadBackgroundImage(
                    apiApp.getUrlImageWithFileName(
                        IMAGENAME_CARD_BACKGROUND
                    )
                )
            })
        }
    }

    LayoutDrawerMenu({
        if (selectEquipe == null) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                buttonDarkStyled("Rafraîchissez vous") { setTriggerEquipe(triggerEquipe.not()) }
                LayoutListSelectableItem(equipes) { setSelectEquipe(it) }
            }
        } else {
            EcranChoixJoueur(selectEquipe, selectedJoueur, {
                selectedJoueur = it
                config.setUserName(it.nom)
            }, bitmapBackground)
        }
    }) {
        TextButton({
            openChangeIpDialog = true
        }) {
            Icon(Icons.Default.Warning, contentDescription = "Adresse Ip")
            Text("Maintenance")
        }
    }

    if (openChangeIpDialog) {
        AlertDialogChangeIp(onCloseChangeIpDialog)
    }


}

@Composable
fun <T : HeadBodyShowable> LayoutListSelectableItem(
    elementsAfficher: List<T>,
    onSelectElement: (T) -> Unit
) {
    LazyColumn {
        items(elementsAfficher) {
            Card(Modifier.fillMaxWidth().padding(15.dp).clickable { onSelectElement(it) }) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        it.getHead(),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        it.getBody(),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}