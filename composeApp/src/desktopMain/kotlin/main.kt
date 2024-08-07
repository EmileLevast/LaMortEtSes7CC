import affichage.LayoutEquipe
import affichage.LayoutStatsJoueur
import affichage.buttonDarkStyled
import affichage.layoutAdmin
import affichage.layoutJoueur
import affichage.layoutListItem
import affichage.layoutMenuConfiguration
import affichage.layoutModeSelection
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lamortetses7cc.composeapp.generated.resources.Res
import lamortetses7cc.composeapp.generated.resources.icon_dark_soul
import network.ApiApp
import org.jetbrains.compose.resources.imageResource
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.core.context.startKoin

fun main() = application {


    startKoin {
        modules(appModule)
    }

    KoinContext {
        AppDesktop(::exitApplication)
    }
}


@Composable
@Preview
fun AppDesktop(onExit: () -> Unit) {
    val state = rememberWindowState(placement = WindowPlacement.Maximized).apply {  }

    Window(
        onCloseRequest = onExit,
        title = "La mort et ses 7 Couvre-chefs",
        state = state,
        icon = BitmapPainter(imageResource(Res.drawable.icon_dark_soul))
    ) {
        MainWindow(onExit)
    }


}

@Composable
fun MainWindow(onExit: () -> Unit) {
    val apiApp = koinInject<ApiApp>()


    val (isInAdminMode, switchAdminMode) = remember { mutableStateOf<Boolean?>(null) }

    val (selectEquipe, setSelectEquipe) = remember { mutableStateOf<Equipe?>(null) }
    val (bitmapBackground, updateBitmapBackground) = remember { mutableStateOf<ImageBitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val (equipes, setEquipes) = remember { mutableStateOf<List<Equipe>>(emptyList()) }

    var openMenuBurger by remember { mutableStateOf(false) }
    val (triggerEquipe, setTriggerEquipe) = remember { mutableStateOf(false) }
    val (isLoading, setLoading) = remember { mutableStateOf(false) }


    LaunchedEffect(triggerEquipe) {
        if(!isLoading){
            coroutineScope.launch {
                setLoading(true)
                setEquipes(withContext(Dispatchers.IO) {//dans un thread à part on maj toute l'equipe
                    apiApp.searchEquipe(".*") ?: listOf()
                })

                updateBitmapBackground(withContext(Dispatchers.IO) {//dans un thread à part on recherche l'image background
                    apiApp.downloadBackgroundImage(
                        apiApp.getUrlImageWithFileName(
                            IMAGENAME_CARD_BACKGROUND
                        )
                    )
                })
                setLoading(false)
            }
        }


    }


    Box(Modifier.fillMaxSize()) {
        //Si on est pas encore decide d'ouvrir l'appli en mode admin ou non
        if (isInAdminMode == null) {
            layoutModeSelection {
                switchAdminMode(it)
            }
        } else if (isInAdminMode) {//si le mode admin est selectionne
            layoutAdmin(bitmapBackground)
        } else {
            //si la liste d'equipe est vide alors on affiche les equipes
            if (selectEquipe == null) {
                LayoutEquipe(equipes) { setSelectEquipe(it) }
                Row(Modifier.align(Alignment.Center)) {
                    buttonDarkStyled("Rafraîchissez vous") { setTriggerEquipe(triggerEquipe.not()) }
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.Black)
                    }
                }
            } else {
                WindowJoueurs(selectEquipe, bitmapBackground)
            }
        }


        Column(Modifier.align(Alignment.TopEnd)) {
            IconButton(
                onClick = { openMenuBurger = !openMenuBurger },
                modifier = Modifier.align(Alignment.End)
            )
            {
                Icon(Icons.Default.Menu, "Menu")
            }

            AnimatedVisibility(
                visible = openMenuBurger,
                enter = slideInHorizontally { it },
                exit = slideOutHorizontally { it }
            ) {
                layoutMenuConfiguration(isInAdminMode, switchAdminMode, onExit)
            }

        }
    }


}


@Composable
fun WindowJoueurs(
    equipeRecherche: Equipe,
    bitmapBackground: ImageBitmap?,
) {
    val apiApp = koinInject<ApiApp>()

    val coroutineScope = rememberCoroutineScope()

    val (joueurs, setJoueurs) = remember { mutableStateOf<List<Joueur>>(emptyList()) }
    val (equipements, setEquipements) = remember { mutableStateOf<List<IListItem>>(emptyList()) }
    var listPinnedItems by remember { mutableStateOf<List<String>>(emptyList()) }
    val (decouvertesEquipe, setDecouvertesEquipe) = remember { mutableStateOf<List<IListItem>?>(null) }
    var selectedJoueur by remember { mutableStateOf(Joueur()) }
    var justClickedJoueur by remember { mutableStateOf(Joueur()) }

    val (triggerDecouverte, setTriggerDecouverte) = remember { mutableStateOf(false) }
    //utilisé pour savoir si du contenu est en train d'etre téléchargé
    var loading by mutableStateOf(false)

    //pour savoir quel élément à afficher en gros
    var equipementToShow by remember { mutableStateOf<IListItem?>(null) }

    LaunchedEffect(justClickedJoueur, equipeRecherche) {
        loading = true
        val updatedAllJoueurs =
            withContext(Dispatchers.IO) {//dans un thread à part on maj tous les joueurs
                apiApp.searchAllJoueur(equipeRecherche.getMembreEquipe())
            }
        setJoueurs(updatedAllJoueurs)//on mets à jour tous les joueurs
        //on remet le joueur actuel a jour avec celui qui a ete selectionne
        selectedJoueur = updatedAllJoueurs.find { it._id == justClickedJoueur._id } ?: justClickedJoueur
        val updatedEquipments = withContext(Dispatchers.IO) {
            apiApp.searchAllEquipementJoueur(selectedJoueur)//on met a jour tout ses equipements
        }
        setEquipements(updatedEquipments)//on les mets sur l'ecran
        listPinnedItems = selectedJoueur.getAllEquipmentSelectionneAsList()
        loading = false
    }

    LaunchedEffect(triggerDecouverte) {
        loading = true
        val updatedEquipes =
            withContext(Dispatchers.IO) {//dans un thread à part on maj toute l'equipe
                apiApp.searchEquipe(equipeRecherche.nom)
            }
        val updatedDecouvertes =
            withContext(Dispatchers.IO) {//dans un thread à part on recherche toutes les decouvertes de l'equipe

                apiApp.searchAllDecouvertesEquipe(updatedEquipes?.firstOrNull() ?: equipeRecherche)
            }

        setDecouvertesEquipe(updatedDecouvertes)//on les mets sur l'ecran
        loading = false
    }

    //Lorsqu'on clique sur un item pour l'ajouter à la liste des items sélectionnés
    //fonction pour ajouter des elements a epingler ou les enlever //true pour epingler l'element
    val togglePinnedItem: (String,Boolean) -> Unit = { nomItem, toPin ->
        if(toPin){
            selectedJoueur.equip(nomItem)
        }else{
            selectedJoueur.unequip(nomItem)
        }
        listPinnedItems = selectedJoueur.getAllEquipmentSelectionneAsList()
        coroutineScope.launch(Dispatchers.IO) { apiApp.updateJoueur(selectedJoueur) }
    }

    //lorsqu'on clique sur un nouveau joueur à sélectionner
    val onSelectedJoueurChange: (Joueur) -> Unit = { clickedJoueur ->
        //on reset l'équipement à montrer en grand
        equipementToShow = null
        //on quitte la fenetre des decouvertes de l'équipe s'il y'en a une
        setDecouvertesEquipe(null)

        justClickedJoueur = clickedJoueur
    }

    //lorsqu'on clique sur les découvertes de l'équipe à afficher
    val onSelectedDecouvertesEquipes: () -> Unit = {
        setTriggerDecouverte(triggerDecouverte.not()) //Juste pour declencher le sideEffect des decouvertes
    }

    val hideBigElement: () -> Unit = {
        equipementToShow = null
    }
    val showBigElement: (IListItem) -> Unit = {
        equipementToShow = it
    }

    //chargement des joueurs
    remember {
        coroutineScope.launch {


            setJoueurs(withContext(Dispatchers.IO) {
                apiApp.searchAllJoueur(equipeRecherche.getMembreEquipe())
            })

        }
    }
    MaterialTheme {


        Column {
            layoutJoueur(
                selectedJoueur,
                onSelectedJoueurChange,
                onSelectedDecouvertesEquipes,
                joueurs
            )
            Row {
                if (decouvertesEquipe != null) {
                    layoutListItem(
                        decouvertesEquipe,
                        bitmapBackground,
                        Modifier.weight(3f),
                        equipementToShow,
                        hideBigElement,
                        showBigElement,
                        false
                    )
                } else {
                    layoutListItem(
                        equipements,
                        bitmapBackground,
                        Modifier.weight(3f),
                        equipementToShow,
                        hideBigElement,
                        showBigElement,
                        true,
                        listPinnedItems = listPinnedItems,
                        togglePinItem = togglePinnedItem
                    )
                }
                LayoutStatsJoueur(selectedJoueur, {
                    selectedJoueur = Joueur(selectedJoueur)
                    coroutineScope.launch(Dispatchers.IO) { apiApp.updateJoueur(selectedJoueur) }
                }, Modifier.weight(1f))
            }
        }
        if (loading) {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.BottomEnd), color = Color.Black)
            }
        }

    }
}