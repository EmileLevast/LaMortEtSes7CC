package affichage

import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject

@Composable
fun buttonDarkStyled(texte:String, onClick:()->Unit){
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()

    FloatingActionButton(modifier = Modifier.padding(graphicsConsts.paddingCellLayoutJoueur), onClick=onClick, backgroundColor = Color.Black) {
        Text(color = Color.White, text = texte,fontFamily = FontFamily(Font(graphicsConsts.fontCard)))
    }
}