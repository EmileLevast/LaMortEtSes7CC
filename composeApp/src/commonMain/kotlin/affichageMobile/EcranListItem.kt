package affichageMobile

import IListItem
import Joueur
import affichage.drawImageWithNetwork
import affichage.layoutBigImage
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import getNbrUtilisationAccordingItem
import lamortetses7cc.composeapp.generated.resources.Res
import lamortetses7cc.composeapp.generated.resources.mainFermee
import lamortetses7cc.composeapp.generated.resources.mainOuverte
import org.jetbrains.compose.resources.painterResource


@Composable
fun EcranListItem(
    equipementsAfficher: List<IListItem>,
    scrollListState: LazyGridState,
    isShowingStats: Boolean,
    isDetailedModeOn: Boolean = false,
    listPinnedItems: List<String>? = null,
    togglePinItem: (String, Boolean) -> Unit = { _: String, _: Boolean -> },
    itemsUtilisations: Map<String, Int>? = null,
    onUtilisationItem: ((IListItem, Int) -> Unit)? = null,
    joueur: Joueur? = null,
    onSave: ()->Unit
) {
    val colorBackground =
        MaterialTheme.colorScheme.tertiaryContainer //necessaire pour l utiliser dans la fonction de drawBehind

    //pour savoir quel élément à afficher en gros
    var equipementToShow by remember { mutableStateOf<IListItem?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = scrollListState,
    ) {
        items(equipementsAfficher) { equipement ->

            val isItemPinned = listPinnedItems?.contains(equipement.nom)

            Card(
                modifier = Modifier.fillMaxWidth().clickable { equipementToShow = equipement }
                    .padding(5.dp),
                border = if (isItemPinned == true) BorderStroke(
                    4.dp,
                    MaterialTheme.colorScheme.primary
                ) else null
            ) {

                Box {

                    Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                        if (isDetailedModeOn) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2,
                                text = equipement.nomComplet.ifBlank { equipement.nom },
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isItemPinned == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.scrim
                            )
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = equipement.getStatsAsStrings(),
                                textAlign = TextAlign.Left,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        } else {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = equipement.nomComplet.ifBlank { equipement.nom },
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isItemPinned == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.scrim
                            )

                            drawImageWithNetwork(
                                equipement,
                                Modifier.align(Alignment.CenterHorizontally).padding(bottom = 10.dp)
                            )
                        }

                    }

                    //Afficher le bouton pin s'il y'a une liste d'items sélectionnés
                    if (isItemPinned == true) {
                        Image(
                            modifier = Modifier.fillMaxWidth(0.3f).align(Alignment.BottomEnd)
                                .clickable {
                                    togglePinItem(equipement.nom, false)
                                },
                            painter = painterResource(Res.drawable.mainFermee),
                            contentScale = ContentScale.Fit,
                            contentDescription = null,

                            )
                    } else if (listPinnedItems != null) {//pour s'assurer qu'on est pas en mode "decouvertes" et donc qu'on ne veut pas afficher les mains
                        Image(
                            modifier = Modifier.fillMaxWidth(0.3f).align(Alignment.BottomEnd)
                                .clickable {
                                    togglePinItem(equipement.nom, true)
                                },
                            painter = painterResource(Res.drawable.mainOuverte),
                            contentScale = ContentScale.Fit,
                            contentDescription = null,
                        )
                    }

                    val nbrUtilisationsRestantes = getNbrUtilisationAccordingItem(
                        equipement,
                        itemsUtilisations?.get(equipement.nom)
                    )
                    Text(
                        modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 15.dp)
                            .fillMaxWidth(0.3f)
                            .clickable {
                                if(onUtilisationItem != null ){
                                    onUtilisationItem(equipement,(nbrUtilisationsRestantes.toInt()-1))
                                }
                            }
                            .drawBehind {
                            drawCircle(
                                color = colorBackground,
                                radius = this.size.height / 2
                            )
                        },
                        text = nbrUtilisationsRestantes,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
        }
    }

    if (equipementToShow != null && !isDetailedModeOn) {
        layoutBigImage(
            equipementToShow!!,
            {itemUsed, nbrUtilisationRestantes->
                if(!isShowingStats){ // si on est sur les decouvertes
                    onSave()
                }
                if(onUtilisationItem != null ){
                    onUtilisationItem(itemUsed,nbrUtilisationRestantes)
                }
                equipementToShow = null },//TODO appeler sauvegarde des utilsiations
            isShowingStats,
            itemsUtilisations?.get(equipementToShow?.nom),
            joueur,
        )
        handleBackButton { equipementToShow = null }
    }

}

@Composable
expect fun handleBackButton(onClickBack: () -> Unit)


