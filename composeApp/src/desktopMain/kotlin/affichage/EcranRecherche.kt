package affichage

import IListItem
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import network.ApiApp
import org.koin.compose.koinInject

@Composable
fun layoutAdmin(
    imageBackground: ImageBitmap?
) {
    var itemsToShow by remember { mutableStateOf<List<IListItem>>(emptyList()) }

    var selectedItemToEdit by remember { mutableStateOf<IListItem?>(null) }

    val onClickItem = { itemClicked: IListItem ->
        selectedItemToEdit = itemClicked
    }

    val onClickBackFromEdition = { clicked: Boolean ->
        selectedItemToEdit = null
    }

    if (selectedItemToEdit != null) {
        layoutEdition(selectedItemToEdit!!, onClickBackFromEdition)
    } else {
        layoutRecherche(imageBackground, itemsToShow, onClickItem) {
            itemsToShow = it
        }
    }
}

@Composable
fun layoutRecherche(
    imageBackground: ImageBitmap?,
    listItems: List<IListItem>,
    onClickItem: (IListItem) -> Unit,
    onChangeListItems: (List<IListItem>) -> Unit
) {
    var nameSearched by remember { mutableStateOf("") }
    var loading by mutableStateOf(false)
    var isDetailedModeOn by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()


    val apiApp = koinInject<ApiApp>()

    val rechercheItems: () -> Unit = {
        coroutineScope.launch {
            if (!loading) {
                loading = true

                val itemsFound = withContext(Dispatchers.IO) {//dans un thread à part on maj toute l'equipe
                    apiApp.searchAnything(nameSearched)
                }
                withContext(Dispatchers.Default) {
                    onChangeListItems(listItems + itemsFound)
                }
                loading = false
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(20.dp).align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.spacedBy(graphicsConsts.cellSpace)) {
            TextField(
                modifier = Modifier.onKeyEvent {
                    if (it.key == Key.Enter) {
                        rechercheItems()
                        true
                    } else {
                        false
                    }
                },
                value = nameSearched,
                onValueChange = {
                    if (it.isBlank() || (it.isNotBlank() && it.last() != '\n')) {
                        nameSearched = it
                    }
                },
            )
            buttonDarkStyled("Valider") {
                rechercheItems()
            }
            buttonDarkStyled("Vider") {
                onChangeListItems(emptyList())
            }
            Switch(
                checked = isDetailedModeOn,
                onCheckedChange = {
                    isDetailedModeOn = it
                }
            )

        }
        layoutListItem(
            listItems,
            imageBackground,
            Modifier,
            null,
            {},
            onClickItem,
            true,
            isDetailedModeOn
        )
    }

    if (loading) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.BottomEnd))
        }
    }
}