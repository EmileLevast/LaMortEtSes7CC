package affichageMobile

import Equipe
import IListItem
import Joueur
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import network.ApiApp
import org.koin.compose.koinInject
import viewModel.FilterViewModel
import viewModel.stateviewmodel.FilterUser

@Composable
fun EcranJoueur(
    selectedJoueur: Joueur, bitmapBackground: ImageBitmap?, selectedEquipe: Equipe, refreshJoueur : Boolean,
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
    if(filterUiState.filterUser == FilterUser.TOUT_EQUIPEMENT){
        EcranEquipement(
            equipements,
            scrollListState,
            true,
            listPinnedItems = listPinnedItems,
            togglePinItem = togglePinnedItem
        )
    }//si la selection c'est l'affichage des statistiques
    else if(filterUiState.filterUser == FilterUser.STATISTIQUES){
        EcranStatistiques(selectedJoueur){
            coroutineScope.launch(Dispatchers.Default) { apiApp.updateJoueur(selectedJoueur) }
        }
    }//sinon on considere que c'est l'affichage des decouvertes
    else{
        EcranDecouverteEquipe(selectedEquipe,refreshJoueur)
    }
}

