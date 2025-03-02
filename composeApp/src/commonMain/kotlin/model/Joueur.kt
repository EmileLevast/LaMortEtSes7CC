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
    var utilisationsRestantesItem:MutableMap<String,Int> = mutableMapOf(),
    var notesPnj:MutableMap<String,String> = mutableMapOf(),//on parse pas ça ce sera juste editable dans l ecran des joueurs
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

    fun equip(itemNom:String){
        chaineEquipementSelectionneSerialisee+= "$CHAR_SEP_EQUIPEMENT$itemNom$CHAR_SEP_EQUIPEMENT"
    }
    fun unequip(itemNom:String){
        chaineEquipementSelectionneSerialisee = chaineEquipementSelectionneSerialisee.replace("$CHAR_SEP_EQUIPEMENT$itemNom$CHAR_SEP_EQUIPEMENT","")
    }

    //retourne vrai s'il y'a eu une modification
    fun setUtilisationsItem(equipement :IListItem, nbrUtilisationsRestantes:Int):Boolean{

        var hasBeenUpdated = false

        val previousUtilisationsRestantes = utilisationsRestantesItem[equipement.nom]
        if(previousUtilisationsRestantes == null){//s'il y'a avait pas d 'utilisations enregistrees pour cet item
            if((equipement is Sort && nbrUtilisationsRestantes != equipement.utilisation) //si c'est un sort et que le nbr utilisations est different que le nombre d utilisation du sort
                ||(equipement !is Sort && nbrUtilisationsRestantes != 1)){ //ou que c'est pas un sort mais qu'on comptabilise plus d'une utilisation
                utilisationsRestantesItem[equipement.nom] = nbrUtilisationsRestantes
                hasBeenUpdated = true
            }
        }else if(nbrUtilisationsRestantes!=previousUtilisationsRestantes){
            //si on a un bien un item et que son nombre d'utilisation est different du precent
            //alors y'a une maj c'est sur
            if((equipement is Sort && nbrUtilisationsRestantes == equipement.utilisation) //si c'est un sort et que le nbr utilisations et au même nombre que le nombre d utilisation du sort
                ||(equipement !is Sort && nbrUtilisationsRestantes == 1)){ //ou que c'est pas un sort mais qu'on ne comptabilise qu'une seule utilisation
                utilisationsRestantesItem.remove(equipement.nom)
            }else {
                utilisationsRestantesItem[equipement.nom] = nbrUtilisationsRestantes
            }
            hasBeenUpdated = true
        }

        return hasBeenUpdated
    }

}