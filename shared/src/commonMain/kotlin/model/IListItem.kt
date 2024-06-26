import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import network.ApiApp

interface IListItem {
    val _id:Int
    val nom:String
    val nomComplet:String
    val color: Color
    var isAttached:Boolean
    val imageName:String
        get() = "${nom.cleanupForDB().replace(" ","")}.jpg"

    fun getStatsAsStrings():String
    fun getStatsSimplifiedAsStrings():String
    fun getParsingRulesAttributesAsList():List<String>
    fun getDeparsedAttributes():List<String>

    fun getBackgroundBorder():String

    fun getImage(apiApp: ApiApp):ImageBitmap
}

