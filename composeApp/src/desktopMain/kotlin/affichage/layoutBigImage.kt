package affichage

import IListItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch
import lamortetses7cc.composeapp.generated.resources.OptimusPrinceps
import lamortetses7cc.composeapp.generated.resources.Res
import lamortetses7cc.composeapp.generated.resources.UnknownImage
import network.ApiApp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.imageResource
import org.koin.compose.koinInject

@Composable
fun layoutBigImage(equipement: IListItem, onClick: () -> Unit, isShowingStats: Boolean) {
    val apiApp = koinInject<ApiApp>()
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val imageToShow = equipement.getImage(apiApp)?: imageResource(Res.drawable.UnknownImage)
    Row(Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxHeight().clickable(onClick = onClick),
            contentScale = ContentScale.Fit,
            bitmap = imageToShow ,
            contentDescription = null,
        )

        Column(modifier = Modifier.weight(1f).draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                coroutineScope.launch {
                    scrollState.scrollBy(-delta)
                }
            },
        ).verticalScroll(scrollState)
            , horizontalAlignment = Alignment.CenterHorizontally) {
            Text(

                text = equipement.nomComplet.ifBlank { equipement.nom },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h2,
                fontFamily = FontFamily(Font(graphicsConsts.fontCard)),
                color = Color.Black
            )

            if(isShowingStats){
                Text(
                    modifier = Modifier.padding(graphicsConsts.statsBigImagePadding),
                    text = equipement.getStatsAsStrings(),
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )
            }

        }
    }

}
