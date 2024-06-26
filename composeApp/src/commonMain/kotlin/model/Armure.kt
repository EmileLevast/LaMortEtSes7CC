import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
class Armure(
    override val nom: String ="inconnu",
    val defense:Map<EffectType,String> = mapOf(),
    val contraintes:String="Aucune contraintes",
    val poids:Int=0,
    val capaciteSpeciale:String="",
    override val nomComplet:String = ""
)
    :ApiableItem(){

    override val _id: Int = nom.hashCode()
    override var isAttached: Boolean = false
    override val color: Color
        get() = Color(0xFF70726E)

    override fun getStatsAsStrings(): String {
        return convertEffectTypeStatsToString(defense)+"\n" +
                strSimplify(contraintes,false)+"\n" +
                "Poids:$poids"+"\n"+
                strSimplify(capaciteSpeciale,false)
    }

    override fun getStatsSimplifiedAsStrings(): String {
        return convertEffectTypeStatsToString(defense)+"\n" +
                strSimplify(contraintes,true)+"\n" +
                "Poids:$poids"+"\n"+
                strSimplify(capaciteSpeciale,true)
    }

    override fun parseFromCSV(listCSVElement : List<String>):ApiableItem {
           return Armure(
               listCSVElement[0].cleanupForDB(),
                parseDefense(listCSVElement[1]),
               listCSVElement[2],
               listCSVElement[3].run{ if(isNotBlank()) toInt() else{0} },
               listCSVElement[4],
               listCSVElement[5]
            )
    }

    override fun getParsingRulesAttributesAsList(): List<String> {
        return listOf(
            "Nom: String",
            "Defense : Format = EffectType:Int|EffectType:Int... (EffectType = Po/Ph/F/Ma)",
            "Contraintes : String",
            "Poids : Int",
            "Capacite speciale : String",
            "nom complet : String"
        )
    }

    override fun getDeparsedAttributes(): List<String> {
        return listOf(
            nom,
            deparseDefense(defense),
            contraintes,
            poids.toString(),
            capaciteSpeciale,
            nomComplet
        )
    }


}

