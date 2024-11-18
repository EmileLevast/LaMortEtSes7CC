package affichageMobile

import Equipe
import Joueur
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import network.ApiApp
import org.koin.compose.koinInject

@Composable
fun EcranChoixJoueur(selectedEquipe: Equipe, bitmapBackground: ImageBitmap?) {
    val apiApp = koinInject<ApiApp>()
    val coroutineScope = rememberCoroutineScope()
    val (joueurs, setJoueurs) = remember { mutableStateOf<List<Joueur>>(emptyList()) }

    LaunchedEffect(selectedEquipe) {
        coroutineScope.launch {
            setJoueurs(withContext(Dispatchers.Unconfined) {//dans un thread à part on maj toute l'equipe
                apiApp.searchAllJoueur(selectedEquipe.getMembreEquipe()) ?: listOf()
            })
        }
    }

    LayoutListSelectableItem(joueurs){

    }
}