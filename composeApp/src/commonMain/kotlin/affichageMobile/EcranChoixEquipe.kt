package affichageMobile

import Equipe
import IMAGENAME_CARD_BACKGROUND
import affichage.buttonDarkStyled
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import network.ApiApp
import org.koin.compose.koinInject

@Composable
fun EcranPrincipal(){
    val apiApp = koinInject<ApiApp>()
    val coroutineScope = rememberCoroutineScope()
    val (equipes, setEquipes) = remember { mutableStateOf<List<Equipe>>(emptyList()) }
    val (triggerEquipe, setTriggerEquipe) = remember { mutableStateOf(false) }
    val (selectEquipe, setSelectEquipe) = remember { mutableStateOf<Equipe?>(null) }

    val (bitmapBackground, updateBitmapBackground) = remember { mutableStateOf<ImageBitmap?>(null) }


    LaunchedEffect(triggerEquipe) {
        coroutineScope.launch {
            setEquipes(withContext(Dispatchers.Main) {//dans un thread à part on maj toute l'equipe
                apiApp.searchEquipe(".*") ?: listOf()
            })

            updateBitmapBackground(withContext(Dispatchers.Main) {//dans un thread à part on recherche l'image background
                apiApp.downloadBackgroundImage(
                    apiApp.getUrlImageWithFileName(
                        IMAGENAME_CARD_BACKGROUND
                    )
                )
            })
        }
    }

    if(selectEquipe == null){
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            buttonDarkStyled("Rafraîchissez vous") { setTriggerEquipe(triggerEquipe.not()) }
            EcranEquipe(equipes) { setSelectEquipe(it) }
        }
    }else{
        EcranAffichageJoueur(selectEquipe, bitmapBackground)
    }

}

@Composable
fun EcranEquipe(
    equipeAfficher: List<Equipe>,
    onSelectEquipe: (Equipe) -> Unit
){
    LazyColumn {
        items(equipeAfficher){
            Card(Modifier.fillMaxWidth().padding(15.dp).clickable { onSelectEquipe(it) }) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(it.nom, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    Text(it.getMembreEquipe().joinToString("\n"), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                }
            }
        }
    }
}