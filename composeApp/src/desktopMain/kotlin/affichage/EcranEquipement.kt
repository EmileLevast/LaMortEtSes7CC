package affichage

import IListItem
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.twotone.Star
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
    isShowingStats : Boolean,
    isDetailedModeOn : Boolean = false,
    isModeAdmin:Boolean =false,
    listPinnedItems :List<Int>? = null,
    togglePinItem:(Int,Boolean)->Unit = { i: Int, b: Boolean -> }
) {

    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()
    val apiApp = koinInject<ApiApp>()

    val scrollState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val listOfHeader:MutableList<Pair<String,Int>> = remember { mutableStateListOf() }
    var positionInRootHeaderOfHeader by remember { mutableStateOf(0) }

    val groupsEquipements = equipementsAfficher.groupBy { it::class.simpleName }

    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 360F,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing)
        )
    )

    Box(Modifier.then(modifier)){
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
            )
        ) {

            groupsEquipements.forEach { (groupName, equipementsGrouped) ->
                header(groupName?:"Xaraxatrailles", graphicsConsts.colorBackgroundSmallHeader, graphicsConsts.colorSmallHeader){ coord ->
                    val indexOfConcernedHeader = listOfHeader.indexOfFirst { it.first == groupName }
                    if(indexOfConcernedHeader >= 0){
                        listOfHeader[indexOfConcernedHeader] = Pair(groupName?:"Vide", coord.positionInRoot().y.toInt())
                    }else{
                        listOfHeader.add(0,Pair(groupName?:"Vide", coord.positionInRoot().y.toInt()))
                    }
                }

                items(equipementsGrouped) { equipement ->
                    Card(
                        modifier = Modifier.fillMaxHeight().clickable { showBigElement(equipement) },
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


                            //Afficher le bouton pin si on est en mode Admin
                            if(isModeAdmin && listPinnedItems!=null){
                                if(listPinnedItems.contains(equipement._id)){
                                    Image(
                                        modifier = Modifier.fillMaxWidth(0.2f).align(Alignment.BottomEnd).clickable{
                                            togglePinItem(equipement._id,false)
                                        }
                                            .graphicsLayer {
                                                rotationY = angle
                                            },
                                        painter = painterResource(Res.drawable.stuff_symbol),
                                        contentScale = ContentScale.Fit,
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(Color.White)
                                    )
                                }else{
                                    Image(
                                        modifier = Modifier.fillMaxWidth(0.14f).align(Alignment.BottomEnd).clickable{
                                            togglePinItem(equipement._id,true)
                                        },
                                        painter = painterResource(Res.drawable.stuff_symbol),
                                        contentScale = ContentScale.Fit,
                                        contentDescription = null,
                                    )
                                }

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
                                        color = Color.White
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