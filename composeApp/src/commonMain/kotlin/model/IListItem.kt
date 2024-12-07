import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import model.HeadBodyShowable
import network.ApiApp
import viewModel.stateviewmodel.FilterUser

interface IListItem : HeadBodyShowable {
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

    fun getImage(apiApp: ApiApp): ImageBitmap?

    override fun getHead(): String = nomComplet.ifBlank { nom }

    override fun getBody(): String = getStatsSimplifiedAsStrings()
}

fun getListItemFiltered(itemsToFilter :List<IListItem>,filterUser: FilterUser, itemsPinned : List<String>?):List<IListItem>{
    return if(filterUser == FilterUser.EQUIPES){
        itemsToFilter.filter { itemsPinned?.contains(it.nom) == true }
    }else{
        itemsToFilter
    }
}

