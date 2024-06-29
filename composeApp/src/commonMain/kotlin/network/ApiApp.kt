package network

import ApiableItem
import Arme
import Armure
import Bouclier
import ENDPOINT_MAJ_CARACS_JOUEUR
import ENDPOINT_RECHERCHE_STRICTE
import ENDPOINT_RECHERCHE_TOUT
import Equipe
import IListItem
import Joueur
import Monster
import Sort
import Special
import configuration.IConfiguration
import extractDecouvertesListFromEquipe
import extractEquipementsListFromJoueur
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import unmutableListApiItemDefinition


class ApiApp(val config: IConfiguration, val imageDownloader: IImageDownloader) {

    val endpoint get() = config.getEndpointServer()

    private val jsonClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }

    }

    suspend fun searchAnything(nomSearched: String, strict: Boolean = false): List<IListItem> {
        try {
            return  deserializeAnythingItemDTO(searchAnythingStringEncoded(nomSearched, strict))
        } catch (e: Exception) {
            println(" ${e.stackTraceToString()} Erreur de connexion au réseau lors de la récupération d'équipement")
            return listOf()
        }
    }

    suspend fun searchAnythingStringEncoded(nomSearched: String, strict: Boolean): List<AnythingItemDTO> {

        jsonClient.get(endpoint + "/$ENDPOINT_RECHERCHE_TOUT" + "/${nomSearched}") {
                url {
                    parameters.append(ENDPOINT_RECHERCHE_STRICTE, strict.toString())
                }
        }.let {
            return if (it.status != HttpStatusCode.NoContent) it.body<List<AnythingItemDTO>>() else listOf()
        }
    }

    suspend fun searchEverything(searchedNames: List<String>, strict:Boolean = false) : List<IListItem> {
        try {
            return  deserializeAnythingItemDTO(searchEverythingStringEncoded(searchedNames))
        } catch (e: Exception) {
            println(" ${e.stackTraceToString()} Erreur de connexion au réseau lors de la récupération d'équipement")

            return listOf()
        }
    }

    fun deserializeAnythingItemDTO(listAnythingItem : List<AnythingItemDTO>): List<IListItem>{
        val listItemsFound = mutableListOf<IListItem>()
        for(anythingItem in listAnythingItem){

            if(anythingItem.itemContent!= null && anythingItem.typeItem != null){
                // Créer une instance de la classe
                val itemClasseReify: ApiableItem? = unmutableListApiItemDefinition.find { it.nameForApi == anythingItem.typeItem }

                //TODO ajouter ici les nouvelles tables a deserialiser
                listItemsFound.add(when(itemClasseReify){
                    is Arme -> Json.decodeFromString<Arme>(anythingItem.itemContent!!)
                    is Armure -> Json.decodeFromString<Armure>(anythingItem.itemContent!!)
                    is Monster -> Json.decodeFromString<Monster>(anythingItem.itemContent!!)
                    is Bouclier -> Json.decodeFromString<Bouclier>(anythingItem.itemContent!!)
                    is Sort -> Json.decodeFromString<Sort>(anythingItem.itemContent!!)
                    is Special -> Json.decodeFromString<Special>(anythingItem.itemContent!!)
                    is Joueur -> Json.decodeFromString<Joueur>(anythingItem.itemContent!!)
                    is Equipe -> Json.decodeFromString<Equipe>(anythingItem.itemContent!!)
                    else-> throw IllegalArgumentException("Impossible de deserialiser l'objet json recu, il ne fait pas parti des elements connus")
                })
            }
        }
        return  listItemsFound
    }

    suspend fun searchEverythingStringEncoded(searchedNames:List<String>) : List<AnythingItemDTO> {

        jsonClient.put("$endpoint/$ENDPOINT_RECHERCHE_TOUT"){
            contentType(ContentType.Application.Json)
            setBody(searchedNames)
        }.let{
            return if (it.status != HttpStatusCode.NoContent) it.body<List<AnythingItemDTO>>() else listOf()
        }
    }

    suspend fun searchArme(nomSearched: String): List<Arme>? {
        jsonClient.get(endpoint + "/" + Arme().namePrecisForApi + "/${nomSearched}").let {
            return if (it.status != HttpStatusCode.NoContent) it.body<List<Arme>>() else null
        }
    }

    suspend fun searchArmure(nomSearched: String): List<Armure>? {
        jsonClient.get(endpoint + "/" + Armure().namePrecisForApi + "/${nomSearched}").let {
            return if (it.status != HttpStatusCode.NoContent) it.body<List<Armure>>() else null
        }
    }

    suspend fun searchBouclier(nomSearched: String): List<Bouclier>? {
        jsonClient.get(endpoint + "/" + Bouclier().namePrecisForApi + "/${nomSearched}").let {
            return if (it.status != HttpStatusCode.NoContent) it.body<List<Bouclier>>() else null
        }
    }

    suspend fun searchSort(nomSearched: String): List<Sort>? {
        jsonClient.get(endpoint + "/" + Sort().namePrecisForApi + "/${nomSearched}").let {
            return if (it.status != HttpStatusCode.NoContent) it.body<List<Sort>>() else null
        }
    }

    suspend fun searchSpecial(nomSearched: String): List<Special>? {
        jsonClient.get(endpoint + "/" + Special().namePrecisForApi + "/${nomSearched}").let {
            return if (it.status != HttpStatusCode.NoContent) it.body<List<Special>>() else null
        }
    }

    suspend fun searchMonster(nomSearched: String): List<Monster>? {
        jsonClient.get(endpoint + "/" + Monster().namePrecisForApi + "/${nomSearched}").let {
            return if (it.status != HttpStatusCode.NoContent) it.body<List<Monster>>() else null
        }
    }

    suspend fun searchJoueur(nomSearched: String): List<Joueur>? {

        try {
            jsonClient.get(endpoint + "/" + Joueur().nameForApi + "/${nomSearched}").let {
                return if (it.status != HttpStatusCode.NoContent) it.body<List<Joueur>>() else null
            }
        } catch (e: Exception) {
            println(" Erreur de connexion au réseau lors de la récupération des joueurs :\n " +
                    e.stackTraceToString()
            )

            return listOf()
        }
    }

    suspend fun searchAllJoueur(listNomSearched: List<String>): List<Joueur> {
        var listJoueurs = mutableListOf<Joueur>()
        listNomSearched.forEach { nameSearched ->
            if (nameSearched.isNotBlank()) {
                //pour chacun des équipements on cherche dans chacune des tables mais on recupere que le premier trouvé
                searchJoueur(nameSearched)?.let { joueurTrouve ->
                    if (joueurTrouve.isNotEmpty()) listJoueurs.add(joueurTrouve.first())
                }
            }
        }
        return listJoueurs
    }

    suspend fun searchEquipe(nomSearched: String): List<Equipe>? {

        try {
            jsonClient.get(endpoint + "/" + Equipe().nameForApi + "/${nomSearched}").let {
                return if (it.status != HttpStatusCode.NoContent) it.body<List<Equipe>>() else null
            }
        } catch (e: Exception) {
            println(" Erreur de connexion au réseau lors de la récupération des equipes :\n " +
                    e.stackTraceToString()
            )
            return listOf()
        }
    }

    suspend fun searchAllEquipementJoueur(joueur: Joueur): List<IListItem> {
        var listEquipements = mutableListOf<IListItem>()
        extractEquipementsListFromJoueur(joueur).let {
            if (it.isNotEmpty()) {
                listEquipements.addAll(searchEverything(it, true))
            }
        }
        return listEquipements
    }

    suspend fun searchAllDecouvertesEquipe(equipe: Equipe): List<IListItem> {
        var listDecouvertes = mutableListOf<IListItem>()
        extractDecouvertesListFromEquipe(equipe).let {
            if (it.isNotEmpty()) {
                //pour chacun des équipements on cherche dans chacune des tables mais on recupere que le premier trouvé
                listDecouvertes.addAll(searchEverything(it,true))
            }
        }
        return listDecouvertes
    }


    /**
     * pour mettre à jour les stats d'un joueur
     */
    suspend fun updateJoueur(joueurToUpdate: Joueur): Boolean {
        jsonClient.post(endpoint + "/" + joueurToUpdate.nameForApi + "/$ENDPOINT_MAJ_CARACS_JOUEUR") {
            contentType(ContentType.Application.Json)
            setBody(joueurToUpdate)
        }.let {
            return it.status == HttpStatusCode.OK
        }
    }

    suspend fun insertItem(itemSelected:ApiableItem):Boolean{
            jsonClient.post(endpoint +"/"+ itemSelected.nameForApi+"/${itemSelected.insertForApi}"){
                contentType(ContentType.Application.Json)
                setBody(itemSelected)
            }.let{
                return it.status== HttpStatusCode.OK
            }
    }

    suspend fun updateItem(itemSelected:ApiableItem):Boolean{
        jsonClient.post(endpoint +"/"+ itemSelected.nameForApi+"/${itemSelected.updateForApi}"){
            contentType(ContentType.Application.Json)
            setBody(itemSelected)
        }.let{
            return it.status== HttpStatusCode.OK
        }
    }

    suspend fun deleteItem(itemSelected:ApiableItem):Boolean{
        jsonClient.post(endpoint +"/"+ itemSelected.nameForApi+"/${itemSelected.deleteForApi}/${itemSelected.nom}"){
        }.let{
            return it.status== HttpStatusCode.OK
        }
    }

    fun downloadImageWithName(imageName: String) = imageDownloader.downloadImageWithName(imageName)

    fun getUrlImageWithFileName(fileName: String) = "$endpoint/images/$fileName"
    fun downloadBackgroundImage(urlImageWithFileName: String) = imageDownloader.downloadBackgroundImage(urlImageWithFileName)

}




