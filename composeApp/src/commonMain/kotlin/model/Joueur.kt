import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

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
    private var utilisationsRestantesItem:MutableMap<String,Int> = mutableMapOf()
) : ApiableItem() {

    override val _id = nom.hashCode()
    override var isAttached = false
    override val color: Color
        get() = Color(0xFFDFAF2C)

    override fun getStatsAsStrings():String{
        return "Niveau : $niveau\n"+getAllEquipmentAsList().joinToString("\n") +
                "\n"+caracActuel.showWithComparisonOriginCarac(caracOrigin)+"\n"+details +"\néquipé:"+ getAllEquipmentSelectionneAsList() +
                getAllUtilisationsRestantesAsString().ifBlank { null }?.let{ "\nUtilisations restantes :$this" }
    }

    override fun parseFromString(listStringElement : List<String>):ApiableItem{
        return Joueur(
            listStringElement[0].cleanupForDB(),
            listStringElement[1],
            listStringElement[2],
            Carac.fromCSV(listStringElement[3]),
            Carac.fromCSV(listStringElement[4]),
            listStringElement[5].getIntOrZero(),
            listStringElement[6],
            listStringElement[7],
            getDeparseAllUtilisationsStringAsMap(listStringElement[8])
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
            "utilisations restantes: ${CHAR_SEP_EQUIPEMENT}String:Int$CHAR_SEP_EQUIPEMENT${CHAR_SEP_EQUIPEMENT}String:Int${CHAR_SEP_EQUIPEMENT}",
        )
    }

    private fun getAllUtilisationsRestantesAsString() : String{
        if(utilisationsRestantesItem.isEmpty()){//s'il n'y a pas d'utilisations on retourne une chaine vide
            return ""
        }
        return CHAR_SEP_EQUIPEMENT+utilisationsRestantesItem.entries.joinToString("$CHAR_SEP_EQUIPEMENT${CHAR_SEP_EQUIPEMENT}") { entry -> "${entry.key}:${entry.value}" }+CHAR_SEP_EQUIPEMENT
    }
    private fun getDeparseAllUtilisationsStringAsMap(parsedStr : String):MutableMap<String,Int>{
        val trimedStr = parsedStr.trim('|')
        if(trimedStr.isBlank()){//s'il n'y a aucune utilisations restantes
            return mutableMapOf() //on retourne une map normale
        }
        return trimedStr.split("||")
            .associate { entry -> entry.substringBefore(':') to entry.substringAfter(':').toInt() }.toMutableMap()
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
            chaineEquipementSelectionneSerialisee,
            getAllUtilisationsRestantesAsString()
        )
    }



    override fun getBody() = "Niveau : $niveau\n" +
            "\n"+caracActuel.showWithComparisonOriginCarac(caracOrigin)

}