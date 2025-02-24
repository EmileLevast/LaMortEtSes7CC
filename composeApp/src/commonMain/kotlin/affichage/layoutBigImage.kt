package affichage

import IListItem
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import configuration.GraphicConstantsFullGrid
import getNbrUtilisationAccordingItem
import org.koin.compose.koinInject

@Composable
fun layoutBigImage(
    equipement: IListItem,
    onClick: (IListItem,Int) -> Unit,
    isShowingStats: Boolean,
    itemUtilisation: Int?,
    ) {
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()
    var nbrUtilisationItem by remember { mutableStateOf(getNbrUtilisationAccordingItem(equipement,itemUtilisation).toInt()) }

    Column(
        Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors()
                .copy(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {

            Column {


                LazyColumn(
                    modifier = Modifier.clickable { onClick(equipement,nbrUtilisationItem) }.padding(10.dp).weight(1f,false),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    item {
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

                    if (isShowingStats) {
                        item {
                            Text(
                                modifier = Modifier.padding(graphicsConsts.statsBigImagePadding),
                                text = equipement.getStatsAsStrings(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer

                            )
                        }
                    }
                }
                val colorBackground =
                    MaterialTheme.colorScheme.tertiary //necessaire pour l utiliser dans la fonction de drawBehind

                Row(
                    Modifier.fillMaxWidth(0.5f).align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    OutlinedButton({
                        nbrUtilisationItem--
                    }) {
                        Text("-")
                    }

                    Text(
                        modifier = Modifier.drawBehind {
                            drawCircle(
                                color = colorBackground,
                                radius = this.size.height / 2
                            )
                        },
                        text = nbrUtilisationItem.toString(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                    )

                    OutlinedButton({
                        nbrUtilisationItem++
                    }) {
                        Text("+")
                    }
                }
            }
        }
    }


}
