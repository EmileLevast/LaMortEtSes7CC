package affichage

import Carac
import Joueur
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import deparseDefense
import getIntOrZeroOrNull
import lamortetses7cc.composeapp.generated.resources.OptimusPrinceps
import lamortetses7cc.composeapp.generated.resources.Res
import lamortetses7cc.composeapp.generated.resources.UnknownImage
import network.ApiApp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.imageResource
import org.koin.compose.koinInject
import kotlin.reflect.KMutableProperty1


@Composable
fun LayoutStatsJoueur(actuelJoueur: Joueur, onSave: () -> Unit, modifier: Modifier = Modifier) {
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()
    val apiApp = koinInject<ApiApp>()

    Column(
        modifier = Modifier.then(modifier)
    ) {

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = actuelJoueur.nomComplet.ifBlank { actuelJoueur.nom },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4,
            fontFamily = FontFamily(Font(graphicsConsts.fontCard))
        )
        Image(
            bitmap = actuelJoueur.getImage(apiApp)?: imageResource(Res.drawable.UnknownImage),
            contentDescription = "avatar",
            contentScale = ContentScale.Fit,            // crop the image if it's not a square
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .weight(1f)
                .clip(CircleShape)                       // clip to the circle shape
                .border(2.dp, Color.Black, CircleShape)   // add a border (optional)
        )
        Text(
            text = "Niveau : ${actuelJoueur.niveau}",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6,
            fontFamily = FontFamily(Font(graphicsConsts.fontCard))
        )
        Text(
            text = "Defense : " + deparseDefense(actuelJoueur.caracOrigin.defense),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6,
            fontFamily = FontFamily(Font(graphicsConsts.fontCard))
        )

        //pour chacune des caracteristiques cites dans cette liste on affiche un layout
        listOf(
            Pair("Vie", Carac::vie),
            Pair("Force", Carac::force),
            Pair("Energie", Carac::energie),
            Pair("Intelligence", Carac::intelligence),
            Pair("Humanite", Carac::humanite),
        ).forEach {
            LayoutCaracSpecificProp(
                it.first,
                actuelJoueur,
                it.second,
                onSave
            )
        }
        Pair("Ames", Carac::ame).let {
            LayoutCaracSpecificProp(
                it.first,
                actuelJoueur,
                it.second,
                onSave,
                true
            )
        }




        TextField(
            value = actuelJoueur.details,
            onValueChange = { actuelJoueur.details = it;onSave() },
            minLines = 4,
            label = { Text("détails") }

        )
    }

}

@Composable
fun LayoutCaracSpecificProp(
    nomCarac: String,
    concernedJoueur: Joueur,
    concernedCarac: KMutableProperty1<Carac, Int>,
    onSave: () -> Unit,
    openDialogWithMultiplesNumber: Boolean = false
) {
    LayoutUneCarac(
        nomCarac,
        concernedCarac.get(concernedJoueur.caracOrigin).toString(),
        concernedCarac.get(concernedJoueur.caracActuel).toString(),
        openDialogWithMultiplesNumber
    ) { oldValue, newValue ->
        concernedCarac.set(
            concernedJoueur.caracActuel,
            if (oldValue == "0" && newValue.isNotBlank() && newValue.last() == '0' && newValue.length == 1) {
                newValue.first().toString().getIntOrZeroOrNull() ?: oldValue.toInt()
            } else {
                newValue.getIntOrZeroOrNull() ?: oldValue.toInt()
            }
        )
        //puis on enregistre le changement
        onSave()
    }
}

@Composable
fun LayoutUneCarac(
    nomCarac: String,
    originCarac: String,
    actuelCarac: String,
    openDialogWithMultiplesNumber: Boolean,
    onTextChange: (String, String) -> Unit,
) {
    val (showDialogMultipleButtons, setShowDialogMultipleButtons) = remember { mutableStateOf(MultipleButtonsDialog.CLOSED) }
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()

    when (showDialogMultipleButtons) {
        MultipleButtonsDialog.ADDITIVE -> {
            dialogAmes (Color.Green){ facteur -> onTextChange(actuelCarac, (actuelCarac.toInt() + facteur).toString()) }
        }

        MultipleButtonsDialog.SOUSTRACTIVE -> {
            dialogAmes (Color.Red){ facteur -> onTextChange(actuelCarac, (actuelCarac.toInt() - facteur).toString()) }
        }

        MultipleButtonsDialog.CLOSED -> {
            //on fait rien
        }
    }


    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$nomCarac($originCarac)",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6,
            fontFamily = FontFamily(Font(graphicsConsts.fontCard))
        )
        TextField(modifier = Modifier.weight(2f),

            value = actuelCarac,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = { onTextChange(actuelCarac, it) }
        )
        FloatingActionButton(modifier = Modifier.weight(1f), backgroundColor = Color.Black,
            onClick = {
                if (!openDialogWithMultiplesNumber) {
                    onTextChange(actuelCarac, (actuelCarac.toInt() + 1).toString())
                } else if(showDialogMultipleButtons == MultipleButtonsDialog.CLOSED) {
                    setShowDialogMultipleButtons(MultipleButtonsDialog.ADDITIVE)
                }else{
                    setShowDialogMultipleButtons(MultipleButtonsDialog.CLOSED)
                }
            }) {
            Text(
                "+",
                color = Color.White,
                fontFamily = FontFamily(Font(graphicsConsts.fontCard))
            )
        }
        FloatingActionButton(modifier = Modifier.weight(1f), backgroundColor = Color.Black,
            onClick = {
                if (!openDialogWithMultiplesNumber) {
                    onTextChange(actuelCarac, (actuelCarac.toInt() - 1).toString())
                } else if(showDialogMultipleButtons == MultipleButtonsDialog.CLOSED){
                    setShowDialogMultipleButtons(MultipleButtonsDialog.SOUSTRACTIVE)
                }else{
                    setShowDialogMultipleButtons(MultipleButtonsDialog.CLOSED)
                }
            }) {
            Text(
                "-",
                color = Color.White,
                fontFamily = FontFamily(Font(graphicsConsts.fontCard))
            )
        }
    }
}

@Composable
fun dialogAmes(color : Color, majAmes: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = 5.dp
    ) {
        modifierLesAmes(color,majAmes)
    }
}

@Composable
fun modifierLesAmes(color : Color,majAmes: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.Center) {
        for (i in arrayOf(1, 10, 100)) {
            FloatingActionButton( backgroundColor = Color.LightGray,onClick = { majAmes(i) }) {
                Text(color = color, text = i.toString())
            }
        }
    }
}

enum class MultipleButtonsDialog {
    CLOSED,
    ADDITIVE,
    SOUSTRACTIVE
}
