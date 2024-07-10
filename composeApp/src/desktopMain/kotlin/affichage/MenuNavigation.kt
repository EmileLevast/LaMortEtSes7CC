package affichage

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import configuration.IConfiguration
import network.ApiApp
import org.koin.compose.koinInject

@Composable
fun layoutMenuConfiguration() {

    var openChangeIpDialog by remember { mutableStateOf(false) }

    val onCloseChangeIpDialog:()->Unit = { openChangeIpDialog = false}

//
//    ModalNavigationDrawer(
//        drawerContent = {
//            ModalDrawerSheet {
//                NavigationDrawerItem(
//                    label = { Text(text = "Adresse Server") },
//                    selected = false,
//                    onClick = { openChangeIpDialog= true}
//                )
//            }
//        }
//    ) {
//        // Screen content
//    }

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
                }
            ) {
                Text("Confirmer")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                }
            ) {
                Text("Annuler")
            }
        }
    )
}

