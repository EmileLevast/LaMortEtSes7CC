package affichage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import configuration.IConfiguration
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject

@Composable
fun layoutMenuConfiguration(
    isInAdminMode: Boolean?,
    switchAdminMode: (Boolean?) -> Unit,
    onExit: () -> Unit
) {

    var openChangeIpDialog by remember { mutableStateOf(false) }
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()

    val onCloseChangeIpDialog:()->Unit = { openChangeIpDialog = false}

    Card(Modifier.fillMaxHeight(),backgroundColor = Color.Black, shape = RoundedCornerShape(10.dp), border = BorderStroke(5.dp, graphicsConsts.brushMenu)) {
        Column(Modifier.padding(graphicsConsts.cellContentPadding)) {
            textButtonMenu("Verboten"){
                openChangeIpDialog = true
            }
            if(isInAdminMode == true){
                textButtonMenu("Mode Utilisateur"){
                    switchAdminMode(false)
                }
            }else{
                textButtonMenu("Unlimited Power !"){
                    switchAdminMode(true)
                }
            }
            textButtonMenu("Barrez-vous cons de mimes!"){
                onExit()
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

@Composable
fun textButtonMenu(texte:String, onClick : ()->Unit){
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()

    TextButton(onClick = onClick){
        Text(texte, color = Color.White, fontFamily = FontFamily(Font(graphicsConsts.fontCard)) )
    }
}

