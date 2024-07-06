package affichage

import ApiableItem
import CHAR_SEP_EQUIPEMENT
import Equipe
import IListItem
import Joueur
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lamortetses7cc.composeapp.generated.resources.OptimusPrinceps
import lamortetses7cc.composeapp.generated.resources.Res
import network.ApiApp
import org.koin.compose.koinInject
import org.jetbrains.compose.resources.Font

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun layoutEdition(
    itemToEdit: IListItem,
    backClick: (Boolean) -> Unit
) {
    var show by remember { mutableStateOf(false) }
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()

    val deparsedAttributs = itemToEdit.getDeparsedAttributes()
    val parsingRulesAttributs = itemToEdit.getParsingRulesAttributesAsList()
    val apiApp = koinInject<ApiApp>()
    val coroutineScope = rememberCoroutineScope()
    val listAttributs = remember {
        mutableStateListOf<String>().apply {
            addAll(deparsedAttributs)
        }
    }

    var listeEquipes by remember { mutableStateOf<List<Equipe>>(emptyList()) }
    var listeJoueurs by remember { mutableStateOf<List<Joueur>>(emptyList()) }

    var message by remember { mutableStateOf<String?>(null) }
    var openAlertDialogDeletion by remember { mutableStateOf(false) }

    LaunchedEffect(message) {
        if (message != null) {
            show = true
            delay(5000)
            message = null
            show = false
        }
    }

    remember {
        coroutineScope.launch {
            listeEquipes = apiApp.searchEquipe(".*") ?: listOf()
            listeJoueurs = apiApp.searchJoueur(".*") ?: listOf()
        }
    }




    Box {

        Row(Modifier.fillMaxSize()) {

            Column(
                Modifier.fillMaxHeight().weight(2f),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box (Modifier.fillMaxWidth()){

                    Row(horizontalArrangement = Arrangement.Start) {
                        Button(onClick = {
                            backClick(true)
                        }) {
                            Text("Retour")
                        }
                    }

                    Row(Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                        Button(onClick = {
                            val itemParsed = (itemToEdit as ApiableItem).parseFromCSV(listAttributs)
                            coroutineScope.launch(Dispatchers.IO) {
                                val res = apiApp.updateItem(itemParsed)
                                withContext(Dispatchers.Default) {
                                    message = if (res) {
                                        "${itemParsed.nom} updated"
                                    } else {
                                        "${itemParsed.nom} - erreur mise à jour"
                                    }
                                }
                            }
                        }) {
                            Text("Update")
                        }
                        Button(onClick = {
                            val itemParsed = (itemToEdit as ApiableItem).parseFromCSV(listAttributs)
                            coroutineScope.launch(Dispatchers.IO) {
                                val res = apiApp.insertItem(itemParsed)
                                withContext(Dispatchers.Default) {
                                    message = if (res) {
                                        "${itemParsed.nom} insere"
                                    } else {
                                        "${itemParsed.nom} - erreur insertion"
                                    }
                                }
                            }
                        }) {
                            Text("Save")
                        }
                        Button(onClick = {
                            openAlertDialogDeletion = true
                        }) {
                            Text("Delete")
                        }
                    }
                }

                //Pour chaque règle de formatage
                LazyColumn(
                    Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    itemsIndexed(parsingRulesAttributs) { index, parsingRules ->
                        if (listAttributs.size > index) {
                            TextField(
                                value = listAttributs[index],
                                onValueChange = { listAttributs[index] = it },
                                label = { Text(parsingRules) }
                            )
                        } else {
                            TextField(
                                value = "ERROR - NO VALUE",
                                onValueChange = { },
                            )
                        }
                    }
                }


            }
            Column(
                Modifier.fillMaxHeight().weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                layoutListeSelectables(
                    Modifier,

                    listeEquipes,
                    { equipe -> (equipe as Equipe).getDecouvertes().contains(itemToEdit.nom) },
                    { isSelect, equipe ->
                        val equipeToUpdate = equipe as? Equipe
                        if (equipeToUpdate != null) {
                            if (isSelect) {
                                equipeToUpdate.chaineDecouvertSerialisee += CHAR_SEP_EQUIPEMENT + itemToEdit.nom + CHAR_SEP_EQUIPEMENT
                            } else {
                                equipeToUpdate.chaineDecouvertSerialisee =
                                    equipeToUpdate.chaineDecouvertSerialisee.replace(
                                        "${CHAR_SEP_EQUIPEMENT}${itemToEdit.nom}$CHAR_SEP_EQUIPEMENT",
                                        ""
                                    )
                            }
                            coroutineScope.launch(Dispatchers.IO) {
                                apiApp.updateItem(equipeToUpdate)
                            }
                        }
                    }
                )
                layoutListeSelectables(
                    Modifier,
                    listeJoueurs,
                    { joueur ->
                        (joueur as Joueur).getAllEquipmentAsList().contains(itemToEdit.nom)
                    },
                    { isSelect, joueur ->
                        val joueurToUpdate = joueur as? Joueur
                        if (joueurToUpdate != null) {
                            if (isSelect) {
                                joueurToUpdate.chaineEquipementSerialisee += CHAR_SEP_EQUIPEMENT + itemToEdit.nom + CHAR_SEP_EQUIPEMENT
                            } else {
                                joueurToUpdate.chaineEquipementSerialisee =
                                    joueurToUpdate.chaineEquipementSerialisee.replace(
                                        "${CHAR_SEP_EQUIPEMENT}${itemToEdit.nom}$CHAR_SEP_EQUIPEMENT",
                                        ""
                                    )
                            }
                            coroutineScope.launch(Dispatchers.IO) {
                                apiApp.updateItem(joueurToUpdate)
                            }
                        }
                    }
                )
            }
        }
        if (show && message != null) {
            Card(
                Modifier.padding(30.dp).align(Alignment.CenterStart),
                backgroundColor = if (message!!.contains("erreur")) Color.Red else Color.DarkGray,
                shape = RoundedCornerShape(3.dp)
            ) {
                Text(
                    text = message!!, color = Color.White,
                    style = MaterialTheme.typography.h5,
                    fontFamily = FontFamily(Font(graphicsConsts.fontCard)),
                )
            }
        }
        if (openAlertDialogDeletion) {
            AlertDialog(
                title = { Text("Supprimer l'élément") },
                onDismissRequest = { openAlertDialogDeletion = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val itemParsed = (itemToEdit as ApiableItem).parseFromCSV(listAttributs)
                            openAlertDialogDeletion = false
                            coroutineScope.launch(Dispatchers.IO) {
                                val res = apiApp.deleteItem(itemParsed)
                                withContext(Dispatchers.Default) {
                                    message = if (res) {
                                        "${itemParsed.nom} suppression"
                                    } else {
                                        "${itemParsed.nom} - erreur suppression"
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Supprimer")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            openAlertDialogDeletion = false
                        }
                    ) {
                        Text("Annuler")
                    }
                }
            )
        }
    }
}

@Composable
fun layoutListeSelectables(
    modifier: Modifier,
    listSelectables: List<IListItem>,
    isDefaultChecked: (IListItem) -> Boolean,
    onSelect: (Boolean, IListItem) -> Unit,
) {
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()

    LazyVerticalGrid(
        modifier = Modifier.then(modifier),
        columns = GridCells.Adaptive(100.dp),
        verticalArrangement = Arrangement.spacedBy(graphicsConsts.cellSpace),
        horizontalArrangement = Arrangement.spacedBy(graphicsConsts.cellSpace),

        ) {
        items(listSelectables) {
            Card(
                Modifier.width(IntrinsicSize.Min),
                backgroundColor = it.color
            ) {
                var checked by remember { mutableStateOf(isDefaultChecked(it)) }
                Column(
                    Modifier.width(IntrinsicSize.Min),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = it.nomComplet.ifBlank { it.nom }, color = Color.White,
                        style = MaterialTheme.typography.body2,
                    )
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { isSelected ->
                            checked = isSelected
                            onSelect(isSelected, it)
                        },
                    )
                }
            }
        }
    }
}

