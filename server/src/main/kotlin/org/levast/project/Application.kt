package org.levast.project

import ApiableItem
import Arme
import Armure
import Bouclier
import ENDPOINT_MAJ_CARACS_JOUEUR
import ENDPOINT_RECHERCHE_STRICTE
import Equipe
import Joueur
import Monster
import QUERY_PARAMETER_NOM
import SERVER_PORT
import Sort
import Special
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import collectionsApiableItem
import com.mongodb.MongoBulkWriteException
import createCollectionTables
import getCollectionElements
import getCollectionElementsAsString
import insertListElements
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import network.AnythingItemDTO
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException

val unmutableListApiItemDefinition = listOf(Arme(),Armure(),Monster(),Bouclier(),Sort(),Special(),Joueur(), Equipe())

val logger = KtorSimpleLogger("logger")

fun main() {


    (LoggerFactory.getILoggerFactory() as LoggerContext).getLogger("org.mongodb.driver").level = Level.WARN

    createCollectionTables()


    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    install(CallLogging) {
        level = org.slf4j.event.Level.INFO

    }
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Put)
        anyHost()
    }
    install(Compression) {
        gzip()
    }
    routing {
        get("/") {
            call.respondText(
                this::class.java.classLoader?.getResource("index.html")!!.readText(),
                ContentType.Text.Html
            )
        }
        static("/") {
            resources("")
        }
        route("/all") {
            put {
                val listNameElementsSearched = call.receive<List<String>>()
                val listItemsFound = mutableListOf<AnythingItemDTO>()

                for (nameElementSearched in listNameElementsSearched) {
                    for (tableObject in unmutableListApiItemDefinition) {
                        getCollectionElementsAsString(tableObject, nameElementSearched, true).map {
                            AnythingItemDTO(
                                tableObject.nameForApi,
                                it
                            )
                        }.let {
                            if (it.isNotEmpty()) {
                                listItemsFound.addAll(it)
                            }
                        }
                    }
                }
                call.respond(listItemsFound.ifEmpty { HttpStatusCode.NoContent })
            }
            get {
                val nom = call.request.queryParameters[QUERY_PARAMETER_NOM] ?: ""
                val rechercheStricte:Boolean = call.request.queryParameters[ENDPOINT_RECHERCHE_STRICTE] == "true"
                val listItemsFound = mutableListOf<AnythingItemDTO>()
                //Pour chaque element on regarde s'il y'en a un qui matche le nom demandé
                for (tableObject in unmutableListApiItemDefinition){
                    getCollectionElementsAsString( tableObject,nom, rechercheStricte).map { AnythingItemDTO(tableObject.nameForApi,it) }.let {
                        if(it.isNotEmpty()){
                            listItemsFound.addAll(it)
                        }
                    }
                }
                call.respond(listItemsFound.ifEmpty { HttpStatusCode.NoContent })
            }
        }
        unmutableListApiItemDefinition.forEach { itapiable ->
            route("/"+itapiable.nameForApi!!){
                get {
                    val nom = call.request.queryParameters[QUERY_PARAMETER_NOM] ?: ""
                    val itemsFound = getCollectionElements(itapiable,nom)
                    call.respond(itemsFound.ifEmpty { HttpStatusCode.NoContent })
                }
                get("/$ENDPOINT_RECHERCHE_STRICTE") {
                    val nom = call.request.queryParameters[QUERY_PARAMETER_NOM] ?: ""
                    val itemsFound = getCollectionElements(itapiable,nom,true)
                    call.respond(itemsFound.ifEmpty { HttpStatusCode.NoContent })
                }
                get("/"+ itapiable.uploadFileForApi) {
                    //retrieve the data from csv file

                    val parsedData:List<ApiableItem> = try {
                        itapiable.decomposeCSV(
                            File("${itapiable.nameForApi}.csv").readLines()
                            .asSequence())
                    } catch (e: FileNotFoundException) {
                        //si le fichier existe pas on retourne une liste vide
                        logger.error(e.stackTraceToString())
                        listOf()
                    }
                    //send data to database
                    try {
                        insertListElements(itapiable,parsedData)
                    } catch (e: MongoBulkWriteException) {
                        logger.error(e.stackTraceToString())
                    }
                    call.respond(parsedData)
                }
                post("/"+ itapiable.updateForApi) {
                    logger.debug("post en cours")

                    val elementToUpdate:ApiableItem = call.receive()

                    val resInsert = collectionsApiableItem[itapiable.nameForApi]!!.updateOneById(elementToUpdate._id,elementToUpdate)

                    if(resInsert.modifiedCount>0){
                        call.respond(HttpStatusCode.OK)
                    }else{
                        call.respond(HttpStatusCode.ExpectationFailed)
                    }
                }
                post("/"+ itapiable.insertForApi) {
                    logger.debug("insert en cours")
                    val elementToInsert:ApiableItem = call.receive()

                    //S'il y'a déjà un élément avec cet identifiant là, on insère pas, faut supprimer avant
                    val resInsert =  if(collectionsApiableItem[itapiable.nameForApi]!!.countDocuments(ApiableItem::_id eq elementToInsert._id) < 1){
                        collectionsApiableItem[itapiable.nameForApi]!!.insertMany(listOf(elementToInsert) as List<Nothing>)
                    } else  { null }

                    if(resInsert?.wasAcknowledged() == true){
                        call.respond(HttpStatusCode.OK)
                    }else{
                        call.respond(HttpStatusCode.ExpectationFailed)
                    }
                }
                post("/"+ itapiable.deleteForApi){
                    val nom = call.request.queryParameters[QUERY_PARAMETER_NOM] ?: ""
                    if(collectionsApiableItem[itapiable.nameForApi]!!.deleteOne(ApiableItem::nom eq nom).wasAcknowledged()){
                        call.respond(HttpStatusCode.OK)
                    }else{
                        call.respond(HttpStatusCode.ExpectationFailed)
                    }
                }
                get("/"+itapiable.downloadForApi) {
                    val itemsFound = getCollectionElements(itapiable,".*")
                    val stringFileCSV = itemsFound.first().getParsingRulesAttributesAsList()
                        .joinToString(";") { it.split(":").first() } + "\n"+
                            itemsFound.map { it.getDeparsedAttributes().joinToString(";") }.joinToString ("\n")
                    val filename = "${itapiable.nameForApi}.csv"
                    val file = File(filename)
                    file.writeText(stringFileCSV)
                    call.response.header(
                        HttpHeaders.ContentDisposition,
                        ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName,
                            filename
                        )
                            .toString()
                    )
                    call.respondFile(file)
                }
                if(itapiable is Joueur){
                    post("/$ENDPOINT_MAJ_CARACS_JOUEUR"){
                        val joueurToUpdateCaracs:Joueur = getApiableElementAccordingToType(call, itapiable) as Joueur

                        val resInsertCaracs = collectionsApiableItem[itapiable.nameForApi]!!.updateOne(filter = Joueur::_id eq joueurToUpdateCaracs._id, update = setValue(Joueur::caracActuel, joueurToUpdateCaracs.caracActuel))
                        val resInsertDetails = collectionsApiableItem[itapiable.nameForApi]!!.updateOne(filter = Joueur::_id eq joueurToUpdateCaracs._id, update = setValue(Joueur::details, joueurToUpdateCaracs.details))

                        if(resInsertCaracs.wasAcknowledged() && resInsertDetails.wasAcknowledged()){
                            call.respond(HttpStatusCode.OK)
                        }else if (resInsertCaracs.wasAcknowledged() || resInsertDetails.wasAcknowledged()){
                            //dans le cas où seulement une des deux données a correctement etait mise à jour
                            call.respond(HttpStatusCode.PartialContent)
                        }
                        else{
                            call.respond(HttpStatusCode.ExpectationFailed)
                        }
                    }
                }
            }
        }
    }
}


/**
 * This function return the object deducing his type
 */
private suspend fun getApiableElementAccordingToType(
    call: ApplicationCall,
    itapiable: ApiableItem
) = when (itapiable) {
    //TODO ajouter une ligne dans le when quand on ajoute un table dans la bdd

    is Arme -> {
        call.receive<Arme>()
    }

    is Monster -> {
        call.receive<Monster>()
    }

    is Armure -> {
        call.receive<Armure>()
    }

    is Bouclier -> {
        call.receive<Bouclier>()
    }

    is Sort -> {
        call.receive<Sort>()
    }

    is Special -> {
        call.receive<Special>()
    }

    is Joueur -> {
        call.receive<Joueur>()
    }

    is Equipe -> {
        call.receive<Equipe>()
    }

    else -> {
        call.receive<Armure>()
    }
}
