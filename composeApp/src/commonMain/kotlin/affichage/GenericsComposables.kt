package affichage

import ApiableItem
import IListItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import configuration.GraphicConstantsFullGrid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lamortetses7cc.composeapp.generated.resources.Res
import lamortetses7cc.composeapp.generated.resources.UnknownImage
import network.ApiApp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun buttonDarkStyled(texte:String, onClick:()->Unit){
    val graphicsConsts = koinInject<GraphicConstantsFullGrid>()

    FloatingActionButton( onClick=onClick) {
        Text(modifier = Modifier.padding(graphicsConsts.paddingCellLayoutJoueur),color = Color.White, text = texte,fontFamily = FontFamily(Font(graphicsConsts.fontCard)))
    }
}

@Composable
fun drawImageWithNetwork(equipement:IListItem, modifier: Modifier=Modifier){
    val apiApp = koinInject<ApiApp>()
    val scope = rememberCoroutineScope()

    var imageToDraw by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(equipement){
        scope.launch(Dispatchers.Default) {
            imageToDraw = equipement.getImage(apiApp)
        }
    }

    if(imageToDraw!=null){
        Image(
            imageToDraw!!,
            contentDescription = "image equipement",
            modifier = modifier.clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
        )
    }else{
        Image(
            painterResource(Res.drawable.UnknownImage),
            contentDescription = "image equipement",
            modifier = modifier.clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
        )
    }

}