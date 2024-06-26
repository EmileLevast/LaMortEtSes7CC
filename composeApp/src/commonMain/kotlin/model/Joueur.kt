import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Joueur(
    override val nom:String="inconnu",
    var chaineEquipementSerialisee: String ="",
    var details: String ="",
    var caracOrigin: Carac = Carac(),
    var caracActuel: Carac = Carac(),
    var niveau:Int=0,
    override val nomComplet:String = ""
) : ApiableItem() {

    override val _id = nom.hashCode()
    override var isAttached = false
    override val color: Color
        get() = Color(0xFFDFAF2C)

    // Copy constructor
    constructor(other: Joueur) : this(
        nom = other.nom,
        chaineEquipementSerialisee = other.chaineEquipementSerialisee,
        details = other.details,
        caracOrigin = Carac(other.caracOrigin),  // Assuming Carac has a copy constructor
        caracActuel = Carac(other.caracActuel),  // Assuming Carac has a copy constructor
        niveau = other.niveau
    )

    override fun getStatsAsStrings():String{
        return "Niveau : $niveau\n"+getAllEquipmentAsList().joinToString("\n") +
                "\n"+caracActuel.showWithComparisonOriginCarac(caracOrigin)+"\n"+details
    }

    override fun parseFromCSV(listCSVElement : List<String>):ApiableItem{
        return Joueur(
            listCSVElement[0].cleanupForDB(),
            listCSVElement[1],
            listCSVElement[2],
            Carac.fromCSV(listCSVElement[3]),
            Carac.fromCSV(listCSVElement[4]),
            listCSVElement[5].getIntOrZero(),
            listCSVElement[6]
        )
    }

    override fun getParsingRulesAttributesAsList(): List<String> {
        return listOf(
            "Nom: String",
            "equipement : ${CHAR_SEP_EQUIPEMENT}String$CHAR_SEP_EQUIPEMENT${CHAR_SEP_EQUIPEMENT}String${CHAR_SEP_EQUIPEMENT}",
            "details : String",
            "caracOrigin : vie/force/EffectType:Int|Effect:Int.../intelligence/energie/humanite/ame",
            "caracActuel : vie/force/EffectType:Int|Effect:Int.../intelligence/energie/humanite/ame",
            "niveau : Int",
            "nom complet : String"
        )
    }

    fun getAllEquipmentAsList()=chaineEquipementSerialisee.deserializeToListElements()

    override fun getDeparsedAttributes(): List<String> {
        return listOf<String>(
            nom,
            chaineEquipementSerialisee,
            details,
            caracOrigin.toCSV(),
            caracActuel.toCSV(),
            niveau.toString(),
            nomComplet
        )
    }
}