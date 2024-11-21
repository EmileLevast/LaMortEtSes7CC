package affichageMobile

import IListItem
import Special
import affichage.CustomPainterCard
import affichage.drawImageWithNetwork
import affichage.layoutBigImage
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import configuration.GraphicConstantsFullGrid
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import lamortetses7cc.composeapp.generated.resources.Res
import lamortetses7cc.composeapp.generated.resources.UnknownImage
import lamortetses7cc.composeapp.generated.resources.mainFermee
import lamortetses7cc.composeapp.generated.resources.mainOuverte
import network.ApiApp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun EcranEquipement(
    equipementsAfficher: List<IListItem>,
    imageBackground: ImageBitmap?,
    modifier: Modifier = Modifier,
    equipementToShow: IListItem?,
    hideBigElement: () -> Unit,
    showBigElement: (IListItem) -> Unit,
    scrollListState: LazyListState,
    isShowingStats: Boolean,
    isDetailedModeOn: Boolean = false,
    listPinnedItems: List<String>? = null,
    togglePinItem: (String, Boolean) -> Unit = { i: String, b: Boolean -> }
) {

    val apiApp = koinInject<ApiApp>()
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()
    val scope = rememberCoroutineScope()



    LazyColumn(
        state = scrollListState,
    ) {
        items(equipementsAfficher) { equipement ->

            val isItemPinned = listPinnedItems?.contains(equipement.nom)

            Card(
                modifier = Modifier.fillMaxWidth().clickable { showBigElement(equipement) },
                border = if(isItemPinned == true) BorderStroke(4.dp, MaterialTheme.colorScheme.onSecondaryContainer) else null
            ) {

                Box {

                    //Si on dipose d'une image de fond et que le mode détails n'est pas activé (le mode détail n'affiche pas les images)
                    if(!isDetailedModeOn){
                        if (imageBackground != null) {
                            drawImageWithNetwork(equipement)
                        }
                        else {
                            Image(
                                painter = painterResource(Res.drawable.UnknownImage),
                                contentDescription = null,
                            )
                        }
                    }

                    //Afficher le bouton pin s'il y'a une liste d'items sélectionnés
                    if(isItemPinned == true){
                        Image(
                            modifier = Modifier.fillMaxWidth(0.3f).align(Alignment.BottomEnd).clickable{
                                togglePinItem(equipement.nom,false)
                            },
                            painter = painterResource(Res.drawable.mainFermee),
                            contentScale = ContentScale.Fit,
                            contentDescription = null,

                            )
                    }else if (listPinnedItems!=null){//pour s'assurer qu'on est pas en mode "decouvertes" et donc qu'on ne veut pas afficher les mains
                        Image(
                            modifier = Modifier.fillMaxWidth(0.5f).align(Alignment.BottomEnd).clickable{
                                togglePinItem(equipement.nom,true)
                            },
                            painter = painterResource(Res.drawable.mainOuverte),
                            contentScale = ContentScale.Fit,
                            contentDescription = null,
                        )
                    }


                    Column(
                    ) {
                        if(isDetailedModeOn){
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = equipement.nomComplet.ifBlank { equipement.nom },
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = equipement.getStatsAsStrings(),
                                textAlign = TextAlign.Left,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }else{
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = equipement.nomComplet.ifBlank { equipement.nom },
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }

                    }
                }
            }
        }
    }

    if (equipementToShow != null && !isDetailedModeOn) {
        layoutBigImage(equipementToShow, hideBigElement, isShowingStats)
    }
}
