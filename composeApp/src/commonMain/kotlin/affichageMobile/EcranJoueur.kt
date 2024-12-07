package affichageMobile

import IListItem
import Joueur
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import network.ApiApp
import org.koin.compose.koinInject

@Composable
fun EcranJoueur(selectedJoueur:Joueur, bitmapBackground: ImageBitmap?){

    val apiApp = koinInject<ApiApp>()
    val coroutineScope = rememberCoroutineScope()

    //Variable pour enregistrer les equipements à afficher
    val (equipements, setEquipements) = remember { mutableStateOf<List<IListItem>>(emptyList()) }
    //pour savoir quel élément à afficher en gros
    var equipementToShow by remember { mutableStateOf<IListItem?>(null) }
    val scrollListState by remember { mutableStateOf<LazyListState>(LazyListState()) }
    var listPinnedItems by remember { mutableStateOf<List<String>>(emptyList()) }

    //Lorsqu'on clique sur un item pour l'ajouter à la liste des items sélectionnés
    //fonction pour ajouter des elements a epingler ou les enlever //true pour epingler l'element
    val togglePinnedItem: (String,Boolean) -> Unit = { nomItem, toPin ->
        if(toPin){
            selectedJoueur.equip(nomItem)
        }else{
            selectedJoueur.unequip(nomItem)
        }
        listPinnedItems = selectedJoueur.getAllEquipmentSelectionneAsList()
        coroutineScope.launch(Dispatchers.Default) { apiApp.updateJoueur(selectedJoueur) }
    }

    LaunchedEffect(selectedJoueur) {
        coroutineScope.launch {

            val updatedEquipments = withContext(Dispatchers.Default) {
                apiApp.searchAllEquipementJoueur(selectedJoueur)//on met a jour tout ses equipements
            }
            setEquipements(updatedEquipments)//on les mets sur l'ecran
            listPinnedItems = selectedJoueur.getAllEquipmentSelectionneAsList()
        }
    }

    EcranEquipement(equipements,
        bitmapBackground,
        Modifier,
        equipementToShow,
        {
            equipementToShow = null
        },
        {
            equipementToShow = it
        },
        scrollListState,
        true,
        listPinnedItems = listPinnedItems,
        togglePinItem = togglePinnedItem
        )


}