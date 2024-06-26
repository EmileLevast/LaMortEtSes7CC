package affichage

import IListItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import network.ApiApp
import org.koin.compose.koinInject

@Composable
fun layoutBigImage(equipement: IListItem, onClick: () -> Unit, isShowingStats: Boolean) {
    val apiApp = koinInject<ApiApp>()
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()

    val imageToShow = equipement.getImage(apiApp)
    Row(Modifier.clickable(onClick = onClick).fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxHeight(),
            contentScale = ContentScale.Fit,
            bitmap = imageToShow,
            contentDescription = null,
        )

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(

                text = equipement.nomComplet.ifBlank { equipement.nom },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h2,
                fontFamily = FontFamily(Font(resource = graphicsConsts.fontCard)),
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
