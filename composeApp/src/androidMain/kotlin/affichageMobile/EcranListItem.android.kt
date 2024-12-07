package affichageMobile

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun handleBackButton(onClickBack: () -> Unit) {
    BackHandler {
        onClickBack()
    }
}