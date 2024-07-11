package affichage

import Equipe
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject

@Composable
fun LayoutEquipe(
    equipeAfficher: List<Equipe>,
    onSelectEquipe: (Equipe) -> Unit
) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()


    Box(Modifier.fillMaxSize()){
        LazyRow(
            state = scrollState,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        coroutineScope.launch {
                            scrollState.scrollBy(-delta)
                        }
                    },
                )
        ) {
            items(equipeAfficher) { equipe ->
                Card(
                    modifier = Modifier.clickable { onSelectEquipe(equipe) }
                        .padding(15.dp),
                    border = BorderStroke(1.dp, Color.Black),
                    elevation = 15.dp,
                ) {
                    Text(
                        text = equipe.nom,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(15.dp),
                        fontFamily = FontFamily(Font(graphicsConsts.fontCard))
                    )
                }
            }
        }
    }

}



