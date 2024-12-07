package affichageMobile

import Equipe
import IListItem
import Joueur
import affichage.drawImageWithNetwork
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lamortetses7cc.composeapp.generated.resources.Res
import lamortetses7cc.composeapp.generated.resources.refreshSymbol
import network.ApiApp
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import viewModel.FilterViewModel
import viewModel.stateviewmodel.FilterUser

@Composable
fun EcranJoueur(
    selectedJoueur: Joueur,
    bitmapBackground: ImageBitmap?,
    selectedEquipe: Equipe,
    refreshJoueur: Boolean,
    filterViewModel: FilterViewModel = viewModel { FilterViewModel() }
) {

    val apiApp = koinInject<ApiApp>()
    val coroutineScope = rememberCoroutineScope()


    //Variable pour enregistrer les equipements à afficher
    val (equipements, setEquipements) = remember { mutableStateOf<List<IListItem>>(emptyList()) }

    val scrollListState by remember { mutableStateOf(LazyListState()) }
    var listPinnedItems by remember { mutableStateOf<List<String>>(emptyList()) }

    //View Model pour savoir l'écran qu'a sélectionné le joueur
    val filterUiState by filterViewModel.uiState.collectAsState()

    //Lorsqu'on clique sur un item pour l'ajouter à la liste des items sélectionnés
    //fonction pour ajouter des elements a epingler ou les enlever //true pour epingler l'element
    val togglePinnedItem: (String, Boolean) -> Unit = { nomItem, toPin ->
        if (toPin) {
            selectedJoueur.equip(nomItem)
        } else {
            selectedJoueur.unequip(nomItem)
        }
        listPinnedItems = selectedJoueur.getAllEquipmentSelectionneAsList()
        coroutineScope.launch(Dispatchers.Default) { apiApp.updateJoueur(selectedJoueur) }
    }

    LaunchedEffect(selectedJoueur, refreshJoueur) {
        coroutineScope.launch {

            val updatedEquipments = withContext(Dispatchers.Default) {
                apiApp.searchAllEquipementJoueur(selectedJoueur)//on met a jour tout ses equipements
            }
            setEquipements(updatedEquipments)//on les mets sur l'ecran
            listPinnedItems = selectedJoueur.getAllEquipmentSelectionneAsList()
        }
    }

    /**
     * Selection des différents écrans
     */
    //si la selection c'est tout les equipements
    if (filterUiState.filterUser == FilterUser.TOUT_EQUIPEMENT) {
        EcranListItem(
            equipements,
            scrollListState,
            true,
            listPinnedItems = listPinnedItems,
            togglePinItem = togglePinnedItem
        )
    }//si la selection c'est l'affichage des statistiques
    else if (filterUiState.filterUser == FilterUser.STATISTIQUES) {
        EcranStatistiques(selectedJoueur) {
            coroutineScope.launch(Dispatchers.Default) { apiApp.updateJoueur(selectedJoueur) }
        }
    }//sinon on considere que c'est l'affichage des decouvertes
    else {
        EcranDecouverteEquipe(selectedEquipe, refreshJoueur)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "rotate"
    )

    //Affichage de l'image et du nom de profil
    Box(Modifier.fillMaxSize()) {
        IconProfilRefreshable(selectedJoueur, Modifier.fillMaxWidth(0.2f).align(Alignment.TopEnd)
            .graphicsLayer {
                rotationZ = rotation
        })
    }
}

@Composable
fun IconProfilRefreshable(selectedJoueur: Joueur, modifier: Modifier = Modifier) {
    Box (modifier.height(IntrinsicSize.Min)){
        Box(Modifier.fillMaxSize(0.55f).align(Alignment.Center)) {
            drawImageWithNetwork(
                selectedJoueur,
                Modifier.clip(CircleShape).align(Alignment.Center)
            )
        }

        Image(
            painterResource(Res.drawable.refreshSymbol),
            "refresh",
            Modifier.align(Alignment.Center),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary)
        )

    }
}

