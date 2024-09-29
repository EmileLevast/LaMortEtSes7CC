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
    override val nomComplet:String = "",
    var chaineEquipementSelectionneSerialisee: String ="",
) : ApiableItem() {

    override val _id = nom.hashCode()
    override var isAttached = false
    override val color: Color
        get() = Color(0xFFDFAF2C)



    override fun getStatsAsStrings():String{
        return "Niveau : $niveau\n"+getAllEquipmentAsList().joinToString("\n") +
                "\n"+caracActuel.showWithComparisonOriginCarac(caracOrigin)+"\n"+details +"\néquipé:"+ getAllEquipmentSelectionneAsList()
    }

    override fun parseFromCSV(listCSVElement : List<String>):ApiableItem{
        return Joueur(
            listCSVElement[0].cleanupForDB(),
            listCSVElement[1],
            listCSVElement[2],
            Carac.fromCSV(listCSVElement[3]),
            Carac.fromCSV(listCSVElement[4]),
            listCSVElement[5].getIntOrZero(),
            listCSVElement[6],
            listCSVElement[7],
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
            "nom complet : String",
            "equipement équipé: ${CHAR_SEP_EQUIPEMENT}String$CHAR_SEP_EQUIPEMENT${CHAR_SEP_EQUIPEMENT}String${CHAR_SEP_EQUIPEMENT}",
        )
    }

    fun getAllEquipmentAsList()=chaineEquipementSerialisee.deserializeToListElements()
    fun getAllEquipmentSelectionneAsList()=chaineEquipementSelectionneSerialisee.deserializeToListElements()
    fun equip(itemNom:String){
        chaineEquipementSelectionneSerialisee+= "$CHAR_SEP_EQUIPEMENT$itemNom$CHAR_SEP_EQUIPEMENT"
    }
    fun unequip(itemNom:String){
        chaineEquipementSelectionneSerialisee = chaineEquipementSelectionneSerialisee.replace("$CHAR_SEP_EQUIPEMENT$itemNom$CHAR_SEP_EQUIPEMENT","")
    }

    override fun getDeparsedAttributes(): List<String> {
        return listOf<String>(
            nom,
            chaineEquipementSerialisee,
            details,
            caracOrigin.toCSV(),
            caracActuel.toCSV(),
            niveau.toString(),
            nomComplet,
            chaineEquipementSelectionneSerialisee
        )
    }
}