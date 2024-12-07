package affichageMobile

import Equipe
import Joueur
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import configuration.IConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import network.ApiApp
import org.koin.compose.koinInject

@Composable
fun EcranChoixJoueur(
    selectedEquipe: Equipe,
    selectedJoueur: Joueur?,
    onSelectedJoueurChange: (Joueur) -> Unit,
    bitmapBackground: ImageBitmap?
) {
    val apiApp = koinInject<ApiApp>()
    val config = koinInject<IConfiguration>()
    var refreshData by remember { mutableStateOf(true) } //à utiliser pour rafraichir tous les launchedEffect
    var isLoadingJoueur by remember { mutableStateOf(false) }


    val coroutineScope = rememberCoroutineScope()
    val (joueurs, setJoueurs) = remember { mutableStateOf<List<Joueur>>(emptyList()) }

    LaunchedEffect(selectedEquipe, refreshData) {
        coroutineScope.launch {
            setJoueurs(withContext(Dispatchers.Default) {//dans un thread à part on maj toute l'equipe
                apiApp.searchAllJoueur(selectedEquipe.getMembreEquipe())
            })
        }
    }

    LaunchedEffect(joueurs) {
        coroutineScope.launch(Dispatchers.Default) {
            if (config.getUserName().isNotBlank()) {//S'il y'a un joueur d'enregistré
                //Alors on set automatiquement le joueur Sélectionné
                joueurs.find { it.nom == config.getUserName() }?.let { onSelectedJoueurChange(it) }
            }
        }
    }

    //s'il n'y a pas de joueur sélectionné on montre la liste des joueurs de l'équipe
    if (selectedJoueur == null) {
        LayoutListSelectableItem(joueurs, onSelectedJoueurChange)
    } else {//Sinon on montre l'écran du joueur
        EcranJoueur(
            selectedJoueur,
            selectedEquipe,
            isLoadingJoueur,
            {
                isLoadingJoueur = false
            },
            refreshData,
            {
                refreshData = refreshData.not()
                isLoadingJoueur = true
                println("REFRESH JOUEUR")
            })//on declenche la mise à jour du joueur

    }
}