package affichageMobile

import Carac
import Joueur
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import configuration.GraphicConstantsFullGrid
import deparseDefense
import getIntOrZeroOrNull
import kotlinx.coroutines.launch
import lamortetses7cc.composeapp.generated.resources.Res
import lamortetses7cc.composeapp.generated.resources.UnknownImage
import network.ApiApp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.imageResource
import org.koin.compose.koinInject
import kotlin.reflect.KMutableProperty1

@Composable
fun EcranStatistiques(actuelJoueur: Joueur, onSave: () -> Unit) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val onSaveDetailsJoueur : (String)->Unit = {
        actuelJoueur.details = it
        onSave()
    }

    LazyColumn(
        Modifier.draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                coroutineScope.launch {
                    scrollState.scrollBy(-delta)
                }
            },
        ),
        state = scrollState,
    ) {
        //Affichage niveau
        item {
            Text(
                text = "Niveau : ${actuelJoueur.niveau}",
                textAlign = TextAlign.Center,
            )
        }
        //Affichage des differents type de defense
        item {
            Text(
                text = "Defense : " + deparseDefense(actuelJoueur.caracOrigin.defense),
                textAlign = TextAlign.Center,
            )
        }

        //affichage des stats restantes
        items(
            listOf(
                Pair("Vie", Carac::vie),
                Pair("Force", Carac::force),
                Pair("Energie", Carac::energie),
                Pair("Intelligence", Carac::intelligence),
                Pair("Humanite", Carac::humanite),
            )
        ) {
            LayoutCaracSpecificProp(
                it.first,
                actuelJoueur,
                it.second,
                onSave
            )
        }

        //affichage des ames
        item {
            Pair("Ames", Carac::ame).let {
                LayoutCaracSpecificProp(
                    it.first,
                    actuelJoueur,
                    it.second,
                    onSave,
                )
            }
        }

        item{
            HorizontalDivider(Modifier.padding(10.dp))
        }

        item {
            layoutDetailJoueur(actuelJoueur.details, onSaveDetailsJoueur)
        }
    }


}

@Composable
fun LayoutCaracSpecificProp(
    nomCarac: String,
    concernedJoueur: Joueur,
    concernedCarac: KMutableProperty1<Carac, Int>,
    onSave: () -> Unit,
) {
    var caracToChange by mutableStateOf(concernedCarac.get(concernedJoueur.caracActuel))

    LayoutUneCarac(
        nomCarac,
        concernedCarac.get(concernedJoueur.caracOrigin).toString(),
        caracToChange.toString()
    ) { oldValue, newValue ->

        //on change sur l'écran la caractéristique
        caracToChange =
            if (oldValue == "0" && newValue.isNotBlank() && newValue.last() == '0' && newValue.length == 1) {
                newValue.first().toString().getIntOrZeroOrNull() ?: oldValue.toInt()
            } else {
                newValue.getIntOrZeroOrNull() ?: oldValue.toInt()
            }

        //on met à jour notre objet joueur
        concernedCarac.set(concernedJoueur.caracActuel, caracToChange)

        //puis on enregistre le changement sur le serveur
        onSave()
    }
}

@Composable
fun LayoutUneCarac(
    nomCarac: String,
    originCarac: String,
    actuelCarac: String,
    onTextChange: (String, String) -> Unit,
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.weight(4f),
            text = "$nomCarac($originCarac)",
            textAlign = TextAlign.Center,
        )

            TextField(
                modifier = Modifier.weight(2f),
                value = actuelCarac,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { onTextChange(actuelCarac, it) }
            )
            FloatingActionButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    onTextChange(actuelCarac, (actuelCarac.toInt() + 1).toString())

                }) {
                Text(
                    "+",
                )
            }
            FloatingActionButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    onTextChange(actuelCarac, (actuelCarac.toInt() - 1).toString())
                }) {
                Text(
                    "-",
                )
            }

    }
}
