package affichage

import IListItem
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import configuration.GraphicConstantsFullGrid
import org.koin.compose.koinInject

@Composable
fun layoutBigImage(equipement: IListItem, onClick: () -> Unit, isShowingStats: Boolean, itemUtilisation: Int?) {
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()

    Column(Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        ){
        Card(){
            LazyColumn (
                modifier = Modifier.clickable { onClick() }.background(MaterialTheme.colorScheme.tertiaryContainer).padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                item{
                    Text(
                        text = equipement.nomComplet.ifBlank { equipement.nom },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                item {
                    drawImageWithNetwork(equipement)
                }

                if(isShowingStats){
                    item{
                        Text(
                            modifier = Modifier.padding(graphicsConsts.statsBigImagePadding),
                            text = equipement.getStatsAsStrings(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer

                        )
                    }

                }
            }
        }
    }



}
