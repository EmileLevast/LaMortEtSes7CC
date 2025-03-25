package affichage

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import configuration.GraphicConstantsFullGrid
import configuration.getGraphicConstants
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject

@Composable
fun buttonDarkStyled(texte:String, onClick:()->Unit){
    val graphicsConsts = getGraphicConstants()

    FloatingActionButton( onClick=onClick) {
        Text(modifier = Modifier.padding(graphicsConsts.paddingCellLayoutJoueur),color = Color.White, text = texte,fontFamily = FontFamily(Font(graphicsConsts.fontCard)))
    }
}