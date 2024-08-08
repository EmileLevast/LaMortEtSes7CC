import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
class Sort(
    override val nom:String="inconnu",
    val sortType:SpellType=SpellType.AME,
    val utilisation:Int=0,
    val cout:String="Aucune",
    val intelligenceMin:Int=0,
    val contraintes:String="Aucune",
    val seuils:List<Seuil> = mutableListOf(),//en cl� c'est le facteur et en valeur c'est la liste des seuils associ�s
    val coupCritiques:String="",
    val iajMax:Int=0,
    val description:String="",
    override val nomComplet:String = ""
) :ApiableItem(){

    override val _id: Int = nom.hashCode()

    override var isAttached: Boolean = false
    override val color: Color
        get() = Color(0xFF00AEEF)

    override fun getStatsAsStrings(): String {
        return statsAsStringAccordingToSimplifyOrNot(false)
    }

    private fun statsAsStringAccordingToSimplifyOrNot(isSimplify:Boolean): String {
        var textSeuils = ""
        seuils.forEach {
            textSeuils += "|   ${it.toPrettyString()}\n"
        }

        val coupCritiquesParsed = strSimplify(coupCritiques, isSimplify)

        return sortType.symbol + "\n" +
                "Utilisations : $utilisation\n" +
                "Cout : $cout\n" +
                "Intelligence Minimum : $intelligenceMin\n" +
                (if (contraintes.isNotBlank()) "$contraintes\n" else "") +
                "Seuils:\n" + textSeuils +
                (if (coupCritiquesParsed.isNotBlank()) "CC : $coupCritiquesParsed\n" else "") +
                "IAJ Max : $iajMax\n" +
                "${strSimplify(description, isSimplify)}\n"
    }

    override fun getStatsSimplifiedAsStrings(): String {
        return statsAsStringAccordingToSimplifyOrNot(true)
    }

    override fun getParsingRulesAttributesAsList(): List<String> {
        return listOf(
            "Nom: String",
            "Type: SpellType = (ame, necromancie, psionique, pyromancie, miracle) ",
            "Utilisation : Int",
            "Cout : String",
            "Intelligence Min : Int",
            "contraintes : String",
            "Seuils: Format = |Int/Int=Effect:Int|EffectType:Int...\\n|Int/Int=Effect:Int|EffectType:Int  ",
            "Coups critiques :String",
            "IAJ Max : Int",
            "Description : String",
            "nom complet : String"
        )
    }



    override fun parseFromCSV(listCSVElement: List<String>): ApiableItem {
        return Sort(
            listCSVElement[0].cleanupForDB(),
            parseSpellType(listCSVElement[1]),
            listCSVElement[2].getIntOrZero(),
            listCSVElement[3],
            listCSVElement[4].getIntOrZero(),
            listCSVElement[5],
            parseSeuils(listCSVElement[6]),
            listCSVElement[7],
            listCSVElement[8].getIntOrZero(),
            listCSVElement[9],
            listCSVElement[10]
        )
    }

    override fun getDeparsedAttributes(): List<String> {

        var textSeuils = ""
        seuils.forEach {
            textSeuils += "|$it\n"
        }

        return listOf<String>(
            nom,
            sortType.name,
            utilisation.toString(),
            cout,
            intelligenceMin.toString(),
            contraintes,
            textSeuils,
            coupCritiques,
            iajMax.toString(),
            description,
            nomComplet
        )
    }

    override fun getBackgroundBorder(): String = "border${sortType.name.lowercase()}.svg"



}