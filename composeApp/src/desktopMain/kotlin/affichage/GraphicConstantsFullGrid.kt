package affichage

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lamortetses7cc.composeapp.generated.resources.Aniron_7BaP
import lamortetses7cc.composeapp.generated.resources.Res

class GraphicConstantsFullGrid {
    val cellMinWidth = 300.dp
    val widthBorder = 2.dp
    val cellSpace = 20.dp
    val cardElevation = 4.dp
    val cellContentPadding = 25.dp
    val statsBigImagePadding = 20.dp
    val fontCard = Res.font.Aniron_7BaP

    val paddingCellLayoutJoueur = 15.dp

    private val colorsSpecialBorder = arrayOf(
        0.0f to Color(0xFFED7F10),
        0.5f to Color.Gray,
        0.6f to Color.White,
        0.75f to Color.Gray,
        0.9f to Color(0xFFED7F10),
    )

    private val colorsBorder = arrayOf(
        0.0f to Color.White,
        0.1f to Color.Gray,
        0.9f to Color.White,
    )

    private val colorsBorderSelected = arrayOf(
        0.0f to Color.White,
        0.1f to Color.Red,
        0.9f to Color.White,
    )

    private val colorsEquipe = arrayOf(
        0.0f to Color(0xFF006400),
        0.8f to Color.LightGray,
        0.95f to Color.White,
    )

    private val colorsJoueursCard = arrayOf(
        0.0f to Color(0xFFED7F10),
        0.8f to Color.LightGray,
        0.95f to Color.White,
    )

    val brushSpecialBorder = Brush.horizontalGradient(colorStops = colorsSpecialBorder)
    val brushBorder = Brush.horizontalGradient(colorStops = colorsBorder)
    val brushBorderSelected = Brush.horizontalGradient(colorStops = colorsBorderSelected)
    val brushMenu = Brush.verticalGradient(colorStops = colorsBorder)
    val brushEquipe = Brush.linearGradient(colorStops = colorsEquipe)
    val brushJoueursCard = Brush.linearGradient(colorStops = colorsJoueursCard)
}