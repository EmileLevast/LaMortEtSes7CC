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
    private var utilisationsRestantesItem:MutableMap<String,Int> = mutableMapOf(),
    private var notesPnj:MutableMap<String,String> = mutableMapOf(),//on parse pas ça ce sera juste editable dans l ecran des joueurs
) : ApiableItem() {

    override val _id = nom.hashCode()
    override var isAttached = false
    override val color: Color
        get() = Color(0xFFDFAF2C)

    override fun getStatsAsStrings():String{
        return "Niveau : $niveau\n"+getAllEquipmentAsList().joinToString("\n") +
                "\n"+caracActuel.showWithComparisonOriginCarac(caracOrigin)+"\n"+details +"\néquipé:"+ getAllEquipmentSelectionneAsList() +
                (getAllUtilisationsRestantesAsString().ifBlank { null }?.let{ "\nUtilisations restantes :$it" } ?: "")
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
            "equipement : $TYPE_LISTE_CHAINE",
            "details : String",
            "caracOrigin : vie/force/EffectType:Int|Effect:Int.../intelligence/energie/humanite/ame",
            "caracActuel : vie/force/EffectType:Int|Effect:Int.../intelligence/energie/humanite/ame",
            "niveau : Int",
            "nom complet : String",
            "equipement équipé: $TYPE_LISTE_CHAINE",
            "utilisations restantes: ${CHAR_SEP_EQUIPEMENT}String:Int$CHAR_SEP_EQUIPEMENT${CHAR_SEP_EQUIPEMENT}String:Int${CHAR_SEP_EQUIPEMENT}",
        )
    }

    private fun getAllUtilisationsRestantesAsString() : String{
        if(utilisationsRestantesItem.isEmpty()){//s'il n'y a pas d'utilisations on retourne une chaine vide
            return ""
        }
        return CHAR_SEP_EQUIPEMENT+utilisationsRestantesItem.entries
            .joinToString("$CHAR_SEP_EQUIPEMENT${CHAR_SEP_EQUIPEMENT}") { entry -> "${entry.key}:${entry.value}" }+CHAR_SEP_EQUIPEMENT
    }
    private fun getDeparseAllUtilisationsStringAsMap(parsedStr : String) = parsedStr.deserializeToListElements()
        ?.associate { entry -> entry.substringBefore(':') to entry.substringAfter(':').toInt() }
        ?.toMutableMap() ?: mutableMapOf()

    fun getAllEquipmentAsList()=chaineEquipementSerialisee.deserializeToListElements()?: listOf()
    fun getAllEquipmentSelectionneAsList()=chaineEquipementSelectionneSerialisee.deserializeToListElements()?: listOf()


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