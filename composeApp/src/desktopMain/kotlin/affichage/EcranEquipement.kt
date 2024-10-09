package affichage

import IListItem
import Special
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import lamortetses7cc.composeapp.generated.resources.Res
import lamortetses7cc.composeapp.generated.resources.UnknownImage
import lamortetses7cc.composeapp.generated.resources.mainFermee
import lamortetses7cc.composeapp.generated.resources.mainOuverte
import lamortetses7cc.composeapp.generated.resources.stuff_symbol
import network.ApiApp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject


@Composable
fun layoutListItem(
    equipementsAfficher: List<IListItem>,
    imageBackground: ImageBitmap?,
    modifier: Modifier = Modifier,
    equipementToShow: IListItem?,
    hideBigElement: () -> Unit,
    showBigElement: (IListItem) -> Unit,
    scrollGridState:LazyGridState,
    isShowingStats : Boolean,
    isDetailedModeOn : Boolean = false,
    listPinnedItems :List<String>? = null,
    togglePinItem:(String,Boolean)->Unit = { i: String, b: Boolean -> }
) {

    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()
    val apiApp = koinInject<ApiApp>()

    val coroutineScope = rememberCoroutineScope()
    val listOfHeader:MutableList<Pair<String,Int>> = mutableStateListOf()
    var positionInRootHeaderOfHeader by remember { mutableStateOf(0) }

    val groupsEquipements = equipementsAfficher.groupBy { it::class.simpleName }

    Box(Modifier.then(modifier)){
        LazyVerticalGrid(
            state = scrollGridState,
            columns = GridCells.Adaptive(minSize = graphicsConsts.cellMinWidth),
            verticalArrangement = Arrangement.spacedBy(graphicsConsts.cellSpace),
            horizontalArrangement = Arrangement.spacedBy(graphicsConsts.cellSpace),
            modifier = Modifier.draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    coroutineScope.launch {
                        scrollGridState.scrollBy(-delta)
                    }
                },
            )
        ) {

            groupsEquipements.forEach { (groupName, equipementsGrouped) ->
                header(groupName?:"Xaraxatrailles", graphicsConsts.colorBackgroundSmallHeader, graphicsConsts.colorSmallHeader){ coord ->
                    val indexOfConcernedHeader = listOfHeader.indexOfFirst { it.first == groupName }
                    val positionY = coord.positionInRoot().y.toInt()

                    //si le header a deja ete enregistre comme apparaissant a l'ecran
                    if(indexOfConcernedHeader >= 0){
                        listOfHeader[indexOfConcernedHeader] = Pair(groupName?:"Vide", positionY)
                    }else{
                        listOfHeader.add(0,Pair(groupName?:"Vide", positionY))
                    }
                }

                items(equipementsGrouped) { equipement ->
                    val isItemPinned = listPinnedItems?.contains(equipement.nom)

                    Card(
                        modifier = Modifier.fillMaxHeight().clickable { showBigElement(equipement) },
                        backgroundColor = equipement.color,
                        elevation = graphicsConsts.cardElevation,
                        border = if(isItemPinned == true) BorderStroke(4.dp,Color.Red) else null
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
                                        painter = CustomPainterCard(imageBackground, equipement.getImage(apiApp) ?: imageResource(Res.drawable.UnknownImage)),
                                        contentDescription = null,
                                    )
                                }
                                else {
                                    Image(
                                        modifier = Modifier.onGloballyPositioned { coordinates ->
                                            // Set column height using the LayoutCoordinates
                                            columnHeightDp = with(localDensity) { coordinates.size.height.toDp() }
                                        },
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
                            }else{
                                Image(
                                    modifier = Modifier.fillMaxWidth(0.3f).align(Alignment.BottomEnd).clickable{
                                        togglePinItem(equipement.nom,true)
                                    },
                                    painter = painterResource(Res.drawable.mainOuverte),
                                    contentScale = ContentScale.Fit,
                                    contentDescription = null,
                                )
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
                                        fontFamily = FontFamily(Font(graphicsConsts.fontCard)),
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
                                        fontFamily = FontFamily(Font(graphicsConsts.fontCard)),
                                        color = if((equipement as? Special)?.itemType == SpecialItemType.TECHNIQUE)graphicsConsts.colorStuffOn else Color.White
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
        if(groupsEquipements.isNotEmpty()){
            Surface (modifier = Modifier.clip(RoundedCornerShape(10.dp)).fillMaxWidth().onGloballyPositioned { coord ->
                positionInRootHeaderOfHeader = coord.positionInRoot().y.toInt()
            }){
                Text(
                    try {
                        listOfHeader.first { it.second<=positionInRootHeaderOfHeader }.first
                    } catch (e: Exception) {
                        "Tu connais cette catégorie ? Moi pas."
                    }, color = graphicsConsts.colorSmallHeader,
                    textAlign = TextAlign.Center, modifier = Modifier.background(graphicsConsts.colorBackgroundSmallHeader).fillMaxWidth())
            }
        }
    }





    if(equipementToShow!=null && !isDetailedModeOn){
        layoutBigImage(equipementToShow, hideBigElement, isShowingStats)
    }
}

fun LazyGridScope.header(
    title: String,
    colorBackground:Color,
    colorFront:Color,
    onChangeLayoutCoordinates: (LayoutCoordinates)->Unit
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = {
        Surface (modifier = Modifier.clip(RoundedCornerShape(10.dp)).onGloballyPositioned(onChangeLayoutCoordinates)){
            Text(title, color = colorFront, textAlign = TextAlign.Center, modifier = Modifier.background(colorBackground)
                    )
        }
    })
}