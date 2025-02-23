

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
class Equipe(
    override val nom:String="inconnu",
    var chaineJoueurSerialisee: String ="",
    override val nomComplet:String = "",
    var chaineDecouvertSerialisee: String ="",
    var chaineDecouvertAnonyme: String ="",
) : ApiableItem() {

    override val _id = nom.hashCode()
    override var isAttached = false
    override val color: Color
        get() = Color(0xFF29BD38)

    override fun getStatsAsStrings():String{
        return "Equipe : \n"+chaineJoueurSerialisee.formatToPrettyString()
    }

    override fun parseFromString(listStringElement : List<String>):ApiableItem{
        return Equipe(
            listStringElement[0].cleanupForDB(),
            listStringElement[1],
            listStringElement[2],
            listStringElement[3],
            listStringElement[4]
        )
    }

    fun getMembreEquipe():List<String>{
        return chaineJoueurSerialisee.deserializeToListElements()?: listOf()
    }

    fun getDecouvertes():List<String>{
        return chaineDecouvertSerialisee.deserializeToListElements()?: listOf()
    }

    override fun getParsingRulesAttributesAsList(): List<String> {

        return listOf(
            "Nom: String",
            "membres : $TYPE_LISTE_CHAINE",
            "nom complet : String",
            "elements découverts : $TYPE_LISTE_CHAINE",
            "elements anonyme: $TYPE_LISTE_CHAINE",
            )
    }

    override fun getDeparsedAttributes(): List<String> {
        return listOf(
            nom,
            chaineJoueurSerialisee,
            nomComplet,
            chaineDecouvertSerialisee,
            chaineDecouvertAnonyme
        )
    }


    override fun getBody()= getMembreEquipe().joinToString("\n")

}