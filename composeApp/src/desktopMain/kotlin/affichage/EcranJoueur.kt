package affichage

import Joueur
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import network.ApiApp
import org.koin.compose.koinInject

@Composable
fun layoutJoueur(
    selectedJoueur: Joueur,
    onSelectedJoueurChange: (Joueur) -> Unit,
    onSelectedDecouvertesEquipe:() ->Unit,
    joueursAfficher: List<Joueur>
) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()


    Row(Modifier.fillMaxWidth()){
        Card(
            modifier = Modifier.clickable {
                onSelectedDecouvertesEquipe()
                }.padding(15.dp),
            border = BorderStroke(1.dp, Color.Magenta),
            elevation = 15.dp,
        ) {
            Text(
                text = "Découvertes",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(15.dp)
            )
        }
        LazyRow(
            state = scrollState,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        coroutineScope.launch {
                            scrollState.scrollBy(-delta)
                        }
                    },
                )
        ) {
            items(joueursAfficher) { joueur ->
                Card(
                    modifier = Modifier
                        .selectable(joueur._id == selectedJoueur._id, onClick = {
                            onSelectedJoueurChange(joueur)
                        }).padding(graphicsConsts.paddingCellLayoutJoueur),
                    border = BorderStroke(1.dp, if (joueur._id == selectedJoueur._id) Color.Red else Color.Black),
                    elevation = graphicsConsts.paddingCellLayoutJoueur,
                ) {
                    nameAndIconPlayer( joueur)

                }
            }
        }
    }

}
@Composable
fun nameAndIconPlayer(joueur: Joueur) {
    val apiApp = koinInject<ApiApp>()
    var columnHeightDp by remember {
        mutableStateOf(0.dp)
    }
    // Get local density from composable
    val localDensity = LocalDensity.current
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()

    Row {
        Image(
            painter = CustomPainterIcon(joueur.getImage(apiApp)),
            contentDescription = "avatar",
            contentScale = ContentScale.Fit,            // crop the image if it's not a square
            modifier = Modifier.height(columnHeightDp)
        )
        Text(
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    // Set column height using the LayoutCoordinates
                    columnHeightDp = with(localDensity) { coordinates.size.height.toDp() }
                }
                .padding(graphicsConsts.paddingCellLayoutJoueur),
            text = joueur.nom,
            style = MaterialTheme.typography.h6,
        )
    }
}


