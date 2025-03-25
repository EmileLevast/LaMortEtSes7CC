package affichageMobile

import Equipe
import IMAGENAME_CARD_BACKGROUND
import Joueur
import affichage.AlertDialogChangeIp
import affichage.LayoutDrawerMenu
import affichage.buttonDarkStyled
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import configuration.IConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lamortetses7cc.composeapp.generated.resources.Res
import lamortetses7cc.composeapp.generated.resources.UnknownImage
import model.HeadBodyShowable
import network.ApiApp
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import viewModel.FilterViewModel
import viewModel.stateviewmodel.FilterModelState
import viewModel.stateviewmodel.FilterUser

@Composable
fun EcranPrincipal(
    filterViewModel: FilterViewModel = viewModel { FilterViewModel() }
) {
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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val onCloseChangeIpDialog: () -> Unit = { openChangeIpDialog = false }
    val filterUiState by filterViewModel.uiState.collectAsState()


    LaunchedEffect(triggerEquipe) {

        coroutineScope.launch {
            withContext(Dispatchers.Default) {
                setEquipes(//dans un thread à part on maj toute l'equipe
                    apiApp.searchEquipe(".*") ?: listOf()
                )
            }

            updateBitmapBackground(withContext(Dispatchers.Default) {//dans un thread à part on recherche l'image background
                apiApp.downloadBackgroundImage(
                    apiApp.getUrlImageWithFileName(
                        IMAGENAME_CARD_BACKGROUND
                    )
                )
            })
        }
    }

    LaunchedEffect(equipes) {
        coroutineScope.launch(Dispatchers.Default) {
            if (nameSavedUser?.isNotBlank() == true) {//S'il y'a un joueur d'enregistré
                //Alors on set automatiquement l'équipe
                setSelectEquipe(equipes.find {
                    it.getMembreEquipe().contains(nameSavedUser)
                })
            }
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
                nameSavedUser = it.nom
            }, bitmapBackground)
        }
    }, {

        /**
         * MENU
         */
        Column(Modifier.width(IntrinsicSize.Min)) {

            selectedJoueur?.let {
                Row {
                    AsyncImage(
                        model = apiApp.createUrlImageFromItem(it),
                        modifier = Modifier.padding(4.dp).clip(CircleShape)
                            .wrapContentWidth(Alignment.End)
                            .fillMaxWidth(0.2f)
                            .border(
                                BorderStroke(2.dp, MaterialTheme.colorScheme.secondary), CircleShape
                            ),
                        contentDescription = null,
                        error = painterResource(Res.drawable.UnknownImage),

                        )

                    Text(
                        it.nomComplet.ifBlank { it.nom },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )

                }

            }

            //Le profil utilisateur
            ItemSimpleMenuButton(
                "Statistiques",
                FilterUser.STATISTIQUES,
                filterViewModel,
                drawerState,
                filterUiState,
            )
            HorizontalDivider()


            //Les catégories d'items
            ItemSimpleMenuButton(
                "Equipés",
                FilterUser.EQUIPES,

                filterViewModel,
                drawerState,
                filterUiState
            )
            ItemSimpleMenuButton(
                "Armes",
                FilterUser.ARMES,
                filterViewModel,
                drawerState,
                filterUiState
            )
            ItemSimpleMenuButton(
                "Sorts",
                FilterUser.SORTS,
                filterViewModel,
                drawerState,
                filterUiState
            )
            ItemSimpleMenuButton(
                "Armures",
                FilterUser.ARMURES,
                filterViewModel,
                drawerState,
                filterUiState
            )
            ItemSimpleMenuButton(
                "Spéciaux",
                FilterUser.SPECIAL,
                filterViewModel,
                drawerState,
                filterUiState
            )
            ItemSimpleMenuButton(
                "Boucliers",
                FilterUser.BOUCLIERS,
                filterViewModel,
                drawerState,
                filterUiState
            )
            ItemSimpleMenuButton(
                "Equipements",
                FilterUser.TOUT_EQUIPEMENT,
                filterViewModel,
                drawerState,
                filterUiState
            )
            ItemSimpleMenuButton(
                "Decouvertes",
                FilterUser.DECOUVERTES,
                filterViewModel,
                drawerState,
                filterUiState
            )
            HorizontalDivider()

            //Les options
            TextButton({
                config.setUserName("")
                nameSavedUser = ""
                setTriggerEquipe(triggerEquipe.not())
                setSelectEquipe(null)
                selectedJoueur = null
                coroutineScope.launch {
                    drawerState.close()
                }
            }) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset joueur")
                Text("Reset sélection")
            }
            TextButton({
                openChangeIpDialog = true
                coroutineScope.launch {
                    drawerState.close()
                }
            }) {
                Icon(Icons.Default.Warning, contentDescription = "Adresse Ip")
                Text("Maintenance")
            }
        }
    }, drawerState)

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

@Composable
fun ItemSimpleMenuButton(
    text: String,
    filter: FilterUser,
    filterViewModel: FilterViewModel,
    drawerState: DrawerState,
    filterUiState: FilterModelState
) {
    val scope = rememberCoroutineScope()

    if (filterUiState.filterUser == filter) {
        OutlinedButton({
            filterViewModel.changeFilterUser(filter)
            scope.launch {
                drawerState.close()
            }
        }) {
            Text(text)
        }
    } else {
        TextButton({
            filterViewModel.changeFilterUser(filter)
            scope.launch {
                drawerState.close()
            }
        }) {
            Text(text)
        }
    }

}