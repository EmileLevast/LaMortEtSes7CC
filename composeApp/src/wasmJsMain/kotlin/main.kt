import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.example.compose.AppTheme
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {

        AppTheme {
            //Redirige vers le code de commonMain
            Text(
                "hey ça marche"
            )
        }

    }
}