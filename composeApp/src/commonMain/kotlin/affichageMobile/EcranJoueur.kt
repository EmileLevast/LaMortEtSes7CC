package affichageMobile

import IListItem
import Joueur
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import network.ApiApp
import org.koin.compose.koinInject

@Composable
fun EcranJoueur(selectedJoueur:Joueur){

    val apiApp = koinInject<ApiApp>()
    val coroutineScope = rememberCoroutineScope()

    //Variable pour enregistrer les equipements à afficher
    val (equipements, setEquipements) = remember { mutableStateOf<List<IListItem>>(emptyList()) }

    LaunchedEffect(selectedJoueur) {
        coroutineScope.launch {

            val updatedEquipments = withContext(Dispatchers.Default) {
                apiApp.searchAllEquipementJoueur(selectedJoueur)//on met a jour tout ses equipements
            }
            setEquipements(updatedEquipments)//on les mets sur l'ecran

        }
    }

    Text("Joueur ${selectedJoueur.nomComplet.ifBlank { selectedJoueur.nom }}")
    LayoutListSelectableItem(equipements){

    }


}