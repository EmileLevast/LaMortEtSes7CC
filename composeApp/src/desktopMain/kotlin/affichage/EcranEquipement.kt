package affichage

import IListItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import network.ApiApp
import org.koin.compose.koinInject


@Composable
fun layoutListItem(
    equipementsAfficher: List<IListItem>,
    imageBackground: ImageBitmap?,
    modifier: Modifier = Modifier,
    equipementToShow: IListItem?,
    hideBigElement: () -> Unit,
    showBigElement: (IListItem) -> Unit,
    isShowingStats : Boolean,
    isDetailedModeOn : Boolean = false
) {

    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()
    val apiApp = koinInject<ApiApp>()


    val scrollState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    LazyVerticalGrid(
        state = scrollState,
        columns = GridCells.Adaptive(minSize = graphicsConsts.cellMinWidth),
        verticalArrangement = Arrangement.spacedBy(graphicsConsts.cellSpace),
        horizontalArrangement = Arrangement.spacedBy(graphicsConsts.cellSpace),
        modifier = Modifier.draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                coroutineScope.launch {
                    scrollState.scrollBy(-delta)
                }
            },
        ).then(modifier)
    ) {
        items(equipementsAfficher) { equipement ->
            Card(
                modifier = if (isDetailedModeOn) {
                    Modifier.fillMaxHeight().clickable { showBigElement(equipement) }
                } else {
                    Modifier.fillMaxHeight()
                },
                backgroundColor = equipement.color,
                elevation = graphicsConsts.cardElevation
            ) {

                Box {
// Get local density from composable
                    val localDensity = LocalDensity.current
                    // Create element height in dp state
                    var columnHeightDp by remember {
                        mutableStateOf(0.dp)
                    }



                    //Si on dipose d'une image de fond et que le mode détails n'est pas activé (le mode détail n'affiche pas les images)
                    if(!isDetailedModeOn){
                        if (imageBackground != null) {
                            Image(
                                modifier = Modifier.onGloballyPositioned { coordinates ->
                                    // Set column height using the LayoutCoordinates
                                    columnHeightDp = with(localDensity) { coordinates.size.height.toDp() }
                                },
                                painter = CustomPainterCard(imageBackground, equipement.getImage(apiApp)),
                                contentDescription = null,
                            )
                        }
                        else {
                            Image(
                                modifier = Modifier.onGloballyPositioned { coordinates ->
                                    // Set column height using the LayoutCoordinates
                                    columnHeightDp = with(localDensity) { coordinates.size.height.toDp() }
                                },
                                painter = painterResource("UnknownImage.jpg"),
                                contentDescription = null,
                            )
                        }
                    }


                    Column(
                        modifier = if (!isDetailedModeOn) {
                            Modifier.padding(graphicsConsts.cellContentPadding)
                                .height(columnHeightDp - (graphicsConsts.cellContentPadding * 2))
                        } else {
                            Modifier
                        }
                    ) {
                        if(isDetailedModeOn){
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = equipement.nomComplet.ifBlank { equipement.nom },
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.h5,
                                fontFamily = FontFamily(Font(resource = graphicsConsts.fontCard)),
                                color = Color.Black
                            )
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = equipement.getStatsAsStrings(),
                                textAlign = TextAlign.Left,
                                style = MaterialTheme.typography.body1,
                                color = Color.Black
                            )
                        }else{
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = equipement.nomComplet.ifBlank { equipement.nom },
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.h5,
                                fontFamily = FontFamily(Font(resource = graphicsConsts.fontCard)),
                                color = Color.White
                            )
                        }
                    }
                }
            }

        }
    }

    if(equipementToShow!=null && !isDetailedModeOn){
        layoutBigImage(equipementToShow, hideBigElement, isShowingStats)
    }
}