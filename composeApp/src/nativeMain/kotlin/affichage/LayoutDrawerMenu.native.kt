package affichage

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable

@Composable
actual fun LayoutDrawerMenu(
    content: @Composable () -> Unit,
    contentOption: @Composable () -> Unit,
    drawerState: DrawerState
) {
}