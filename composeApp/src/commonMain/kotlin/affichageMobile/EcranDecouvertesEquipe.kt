package affichageMobile

import Equipe
import IListItem
import Joueur
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import configuration.getApiApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import network.ApiApp
import org.koin.compose.koinInject


@Composable
fun EcranDecouverteEquipe(
    selectedEquipe: Equipe,
    refreshDecouvertes: Boolean,
    joueur: Joueur,
    onSave: () -> Unit
) {
    val (decouvertesEquipe, setDecouvertesEquipe) = remember {
        mutableStateOf<List<IListItem>>(
            emptyList()
        )
    }

    val apiApp = getApiApp()
    val scrollListState by remember { mutableStateOf(LazyGridState()) }


    LaunchedEffect(refreshDecouvertes, selectedEquipe) {
        val updatedEquipes =
            withContext(Dispatchers.Default) {//dans un thread à part on maj toute l'equipe
                apiApp.searchEquipe(selectedEquipe.nom)
            }
        val updatedDecouvertes =
            withContext(Dispatchers.Default) {//dans un thread à part on recherche toutes les decouvertes de l'equipe
                apiApp.searchAllDecouvertesEquipe(updatedEquipes?.firstOrNull() ?: selectedEquipe)
            }

        setDecouvertesEquipe(updatedDecouvertes)//on les mets sur l'ecran
    }

    EcranListItem(
        decouvertesEquipe,
        scrollListState,
        false,
        joueur = joueur,
        onSave = onSave
    )

}