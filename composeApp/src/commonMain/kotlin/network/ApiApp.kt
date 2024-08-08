package network

import ApiableItem
import Arme
import Armure
import Bouclier
import ENDPOINT_MAJ_CARACS_JOUEUR
import ENDPOINT_RECHERCHE_STRICTE
import ENDPOINT_RECHERCHE_TOUT
import ERROR_NETWORK_MESSAGE
import Equipe
import IListItem
import Joueur
import Monster
import QUERY_PARAMETER_NOM
import Sort
import Special
import androidx.compose.ui.graphics.ImageBitmap
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
            level = LogLevel.INFO
        }

    }

    suspend fun searchAnything(nomSearched: String, strict: Boolean = false): List<IListItem> {
        return deserializeAnythingItemDTO(searchAnythingStringEncoded(nomSearched, strict))
    }

    private suspend fun searchAnythingStringEncoded(
        nomSearched: String,
        strict: Boolean
    ): List<AnythingItemDTO> {
        return catchNetworkError(defaultReturnValue = listOf()) {
            jsonClient.get("$endpoint/$ENDPOINT_RECHERCHE_TOUT") {
                url {
                    parameters.append(ENDPOINT_RECHERCHE_STRICTE, strict.toString())
                    parameters.append(QUERY_PARAMETER_NOM, nomSearched)
                }
            }.let {
                if (it.status != HttpStatusCode.NoContent) it.body<List<AnythingItemDTO>>() else listOf()
            }
        }
    }

    private suspend fun searchEverything(
        searchedNames: List<String>,
    ): List<IListItem> {
        return deserializeAnythingItemDTO(searchEverythingStringEncoded(searchedNames))
    }

    private fun deserializeAnythingItemDTO(listAnythingItem: List<AnythingItemDTO>): List<IListItem> {
        val listItemsFound = mutableListOf<IListItem>()
        for (anythingItem in listAnythingItem) {

            if (anythingItem.itemContent != null && anythingItem.typeItem != null) {
                // Créer une instance de la classe
                val itemClasseReify: ApiableItem? =
                    unmutableListApiItemDefinition.find { it.nameForApi == anythingItem.typeItem }

                //TODO ajouter ici les nouvelles tables a deserialiser
                listItemsFound.add(
                    when (itemClasseReify) {
                        is Arme -> Json.decodeFromString<Arme>(anythingItem.itemContent!!)
                        is Armure -> Json.decodeFromString<Armure>(anythingItem.itemContent!!)
                        is Monster -> Json.decodeFromString<Monster>(anythingItem.itemContent!!)
                        is Bouclier -> Json.decodeFromString<Bouclier>(anythingItem.itemContent!!)
                        is Sort -> Json.decodeFromString<Sort>(anythingItem.itemContent!!)
                        is Special -> Json.decodeFromString<Special>(anythingItem.itemContent!!)
                        is Joueur -> Json.decodeFromString<Joueur>(anythingItem.itemContent!!)
                        is Equipe -> Json.decodeFromString<Equipe>(anythingItem.itemContent!!)
                        else -> throw IllegalArgumentException("Impossible de deserialiser l'objet json recu, il ne fait pas parti des elements connus")
                    }
                )
            }
        }
        return listItemsFound
    }

    private suspend fun searchEverythingStringEncoded(searchedNames: List<String>): List<AnythingItemDTO> {
        return catchNetworkError(defaultReturnValue = listOf()) {
            jsonClient.put("$endpoint/$ENDPOINT_RECHERCHE_TOUT") {
                contentType(ContentType.Application.Json)
                setBody(searchedNames)
            }.let {
                if (it.status != HttpStatusCode.NoContent) it.body<List<AnythingItemDTO>>() else listOf()
            }
        }
    }

    suspend fun searchJoueur(nomSearched: String): List<Joueur>? {
        return catchNetworkError(defaultReturnValue = listOf()) {

            jsonClient.get(endpoint + "/" + Joueur().nameForApi) {
                url {
                    parameters.append(QUERY_PARAMETER_NOM, nomSearched)
                }
            }.let {
                if (it.status != HttpStatusCode.NoContent) it.body<List<Joueur>>() else null
            }
        }
    }

    suspend fun searchAllJoueur(listNomSearched: List<String>): List<Joueur> {
        val listJoueurs = mutableListOf<Joueur>()
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
        return catchNetworkError(defaultReturnValue = listOf()) {
            jsonClient.get(endpoint + "/" + Equipe().nameForApi) {
                url {
                    parameters.append(QUERY_PARAMETER_NOM, nomSearched)
                }
            }.let {
                if (it.status != HttpStatusCode.NoContent) it.body<List<Equipe>>() else null
            }
        }
    }

    suspend fun searchAllEquipementJoueur(joueur: Joueur): List<IListItem> {
        val listEquipements = mutableListOf<IListItem>()
        extractEquipementsListFromJoueur(joueur).let {
            if (it.isNotEmpty()) {
                listEquipements.addAll(searchEverything(it))
            }
        }
        return listEquipements
    }

    suspend fun searchAllDecouvertesEquipe(equipe: Equipe): List<IListItem> {
        var listDecouvertes = mutableListOf<IListItem>()
        extractDecouvertesListFromEquipe(equipe).let {
            if (it.isNotEmpty()) {
                //pour chacun des équipements on cherche dans chacune des tables mais on recupere que le premier trouvé
                listDecouvertes.addAll(searchEverything(it))
            }
        }
        return listDecouvertes
    }


    /**
     * pour mettre à jour les stats d'un joueur
     */
    suspend fun updateJoueur(joueurToUpdate: Joueur): Boolean {
        return catchNetworkError(defaultReturnValue = false) {
            jsonClient.post(endpoint + "/" + joueurToUpdate.nameForApi + "/$ENDPOINT_MAJ_CARACS_JOUEUR") {
                contentType(ContentType.Application.Json)
                setBody(joueurToUpdate)
            }.let {
                it.status == HttpStatusCode.OK
            }
        }
    }

    suspend fun insertItem(itemSelected: ApiableItem): Boolean {
        return catchNetworkError(defaultReturnValue = false) {
            jsonClient.post(endpoint + "/" + itemSelected.nameForApi + "/${itemSelected.insertForApi}") {
                contentType(ContentType.Application.Json)
                setBody(itemSelected)
            }.let {
                it.status == HttpStatusCode.OK
            }
        }
    }

    suspend fun updateItem(itemSelected: ApiableItem): Boolean {
        return catchNetworkError(defaultReturnValue = false) {
            jsonClient.post(endpoint + "/" + itemSelected.nameForApi + "/${itemSelected.updateForApi}") {
                contentType(ContentType.Application.Json)
                setBody(itemSelected)
            }.let {
                it.status == HttpStatusCode.OK
            }
        }
    }

    suspend fun deleteItem(itemSelected: ApiableItem): Boolean {
        return catchNetworkError(defaultReturnValue = false) {
            jsonClient.post(endpoint + "/" + itemSelected.nameForApi + "/${itemSelected.deleteForApi}") {
                url {
                    parameters.append(QUERY_PARAMETER_NOM, itemSelected.nom)
                }
            }.let {
                it.status == HttpStatusCode.OK
            }
        }
    }

    fun downloadImageWithName(imageName: String): ImageBitmap? {
        return catchNetworkErrorUnsuspendly(defaultReturnValue = null) {
            imageDownloader.downloadImageWithName(imageName)
        }
    }

    fun getUrlImageWithFileName(fileName: String) = "$endpoint/images/$fileName"
    fun downloadBackgroundImage(urlImageWithFileName: String): ImageBitmap? {
        return catchNetworkErrorUnsuspendly(defaultReturnValue = null) {
            imageDownloader.downloadBackgroundImage(urlImageWithFileName)
        }
    }


    private suspend fun <T> catchNetworkError(
        errorMessage: String = ERROR_NETWORK_MESSAGE,
        defaultReturnValue: T,
        networkAction: suspend () -> T,
    ): T {
        return try {
            networkAction()
        } catch (e: Exception) {
            println(
                " $errorMessage\n " +
                        e.stackTraceToString()
            )
            return defaultReturnValue
        }
    }

    private fun <T> catchNetworkErrorUnsuspendly(
        errorMessage: String = ERROR_NETWORK_MESSAGE,
        defaultReturnValue: T,
        networkAction: () -> T,
    ): T {
        return try {
            networkAction()
        } catch (e: Exception) {
            println(
                " $errorMessage\n " +
                        e.stackTraceToString()
            )
            return defaultReturnValue
        }
    }
}




