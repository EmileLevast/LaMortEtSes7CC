package affichage

import IListItem
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import network.ApiApp
import org.koin.compose.koinInject
import viewModel.AdminViewModel

@Composable
fun layoutAdmin(
    imageBackground: ImageBitmap?
) {

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
        layoutRecherche(imageBackground, onClickItem)
    }
}

@Composable
fun layoutRecherche(
    imageBackground: ImageBitmap?,
    onClickItem: (IListItem) -> Unit,
    adminViewModel: AdminViewModel= viewModel{ AdminViewModel() }
) {
    var nameSearched by remember { mutableStateOf("") }
    var loading by mutableStateOf(false)
    var isDetailedModeOn by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()

    val adminUiState by adminViewModel.uiState.collectAsState()

    //contient la liste des id des items qui sont references comme ne devant pas être supprimés
    val listPinnedItem = remember { mutableStateListOf<Int>()}


    val apiApp = koinInject<ApiApp>()

    val rechercheItems: () -> Unit = {
        coroutineScope.launch {
            if (!loading) {
                loading = true

                val itemsFound = withContext(Dispatchers.IO) {//dans un thread à part on maj toute l'equipe
                    apiApp.searchAnything(nameSearched)
                }
                withContext(Dispatchers.Default) {
                    adminViewModel.addAllToItems(*itemsFound.toTypedArray())
                }
                loading = false
            }
        }
    }

    //fonction pour ajouter des elements a epingler ou les enlever //true pour epingler l'element
    val togglePinnedItem: (Int,Boolean) -> Unit = { id, toPin ->
        if(toPin){
            listPinnedItem.add(id)
        }else{
            listPinnedItem.remove(id)
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
                adminViewModel.deleteIf { item-> !listPinnedItem.contains(item._id) }
            }
            Switch(
                checked = isDetailedModeOn,
                onCheckedChange = {
                    isDetailedModeOn = it
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = Color.DarkGray,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray,
                )
            )

        }
        layoutListItem(
            adminUiState.listitems,
            imageBackground,
            Modifier,
            null,
            {},
            onClickItem,
            true,
            isDetailedModeOn,
            true,
            listPinnedItem,
            togglePinnedItem
        )
    }

    if (loading) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.BottomEnd), color = Color.Black)
        }
    }
}