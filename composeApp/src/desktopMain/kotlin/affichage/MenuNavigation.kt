package affichage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Shapes
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import configuration.IConfiguration
import network.ApiApp
import org.koin.compose.koinInject

@Composable
fun layoutMenuConfiguration() {

    var openChangeIpDialog by remember { mutableStateOf(false) }

    val onCloseChangeIpDialog:()->Unit = { openChangeIpDialog = false}
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()

    Card(Modifier.fillMaxHeight(),backgroundColor = Color.Black, shape = RoundedCornerShape(10.dp), border = BorderStroke(10.dp, Color.Gray)) {
        Column(Modifier.padding(graphicsConsts.cellContentPadding)) {
            TextButton(onClick = {
                openChangeIpDialog = true
            }){
                Text("Changer Adresse", color = Color.White )
            }
        }
    }

    if(openChangeIpDialog){
        AlertDialogChangeIp(onCloseChangeIpDialog)
    }
}

@Composable
fun AlertDialogChangeIp(
    onDismissRequest: ()->Unit
) {
    val config = koinInject<IConfiguration>()

    var ipAdressInput by remember { mutableStateOf(config.getIpAdressTargetServer()) }

    AlertDialog(
        title = {
            Text(text = "ChangeIp")
        },
        text = {
            TextField(
                value = ipAdressInput,
                onValueChange = { ipAdressInput = it },
                label = { Text("ip") }
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    config.setIpAdressTargetServer(ipAdressInput)
                    onDismissRequest()
                }
            ) {
                Text("Confirmer")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Annuler")
            }
        }
    )
}

