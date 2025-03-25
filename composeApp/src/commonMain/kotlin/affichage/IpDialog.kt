package affichage

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import configuration.IConfiguration
import configuration.getConfiguration
import org.koin.compose.koinInject


@Composable
fun AlertDialogChangeIp(
    onDismissRequest: ()->Unit
) {
    val config = getConfiguration()

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
