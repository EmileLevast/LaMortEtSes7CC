package affichage

import IListItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import configuration.GraphicConstantsFullGrid
import kotlinx.coroutines.launch
import lamortetses7cc.composeapp.generated.resources.Res
import lamortetses7cc.composeapp.generated.resources.UnknownImage
import network.ApiApp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.imageResource
import org.koin.compose.koinInject

@Composable
fun layoutBigImage(equipement: IListItem, onClick: () -> Unit, isShowingStats: Boolean) {
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()

    Column(Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        ){
        Card(Modifier.padding(10.dp)){
            Column(
                modifier = Modifier.clickable { onClick() },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                drawImageWithNetwork(equipement)

                Text(
                    text = equipement.nomComplet.ifBlank { equipement.nom },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                )

                if(isShowingStats){
                    Text(
                        modifier = Modifier.padding(graphicsConsts.statsBigImagePadding),
                        text = equipement.getStatsAsStrings(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }



}
