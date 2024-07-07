import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.UpdateResult
import io.ktor.server.application.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.regex

val client = KMongo.createClient().coroutine
val database = client.getDatabase("JDRProd_Backup")

val collectionsApiableItem:MutableMap<String,CoroutineCollection<out ApiableItem>> = mutableMapOf()

fun createCollectionTables(){
    unmutableListApiItemDefinition.forEach {
        collectionsApiableItem[it.nameForApi!!] = database.getCollection(it.nameForApi!!)
    }
}

//TODO ajouter ici une ligne dans le when a chaque fois qu'eun nouvelle collection dans la bdd est cree
/**
 * strict est un booleen quand il est à true c'est à dire qu'on cherche exactement l'element qui matche le nom
 */
suspend fun insertListElements(instanceOfCollectionItemDefinition:ApiableItem,listToInsert:List<ApiableItem>): Any {
    return when(instanceOfCollectionItemDefinition){
        is Arme ->  database.getCollection<Arme>().insertMany(listToInsert as List<Arme>)
        is Armure -> database.getCollection<Armure>().insertMany(listToInsert as List<Armure>)
        is Monster -> database.getCollection<Monster>().insertMany(listToInsert as List<Monster>)
        is Bouclier -> database.getCollection<Bouclier>().insertMany(listToInsert as List<Bouclier>)
        is Sort -> database.getCollection<Sort>().insertMany(listToInsert as List<Sort>)
        is Special -> database.getCollection<Special>().insertMany(listToInsert as List<Special>)
        is Joueur -> database.getCollection<Joueur>().insertMany(listToInsert as List<Joueur>)
        is Equipe -> database.getCollection<Equipe>().insertMany(listToInsert as List<Equipe>)
        else-> Unit
    }
}

//TODO ajouter ici une ligne dans le when a chaque fois qu'eun nouvelle collection dans la bdd est cree
/**
 * strict est un booleen quand il est à true c'est à dire qu'on cherche exactement l'element qui matche le nom
 */
suspend fun getCollectionElements(instanceOfCollectionItemDefinition:ApiableItem, nameOfItemSearched:String, strict:Boolean =false):List<ApiableItem>{
    return when(instanceOfCollectionItemDefinition){
        is Arme -> getElementAccordingToType(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Armure -> getElementAccordingToType(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Monster -> getElementAccordingToType(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Bouclier -> getElementAccordingToType(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Sort -> getElementAccordingToType(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Special -> getElementAccordingToType(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Joueur -> getElementAccordingToType(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Equipe -> getElementAccordingToType(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        else-> getElementAccordingToType(nameOfItemSearched, instanceOfCollectionItemDefinition as Monster,strict)
    }
}

//TODO ici aussi
/**
 * strict est un booleen quand il est à true c'est à dire qu'on cherche exactement l'element qui matche le nom
 */
suspend fun getCollectionElementsAsString(instanceOfCollectionItemDefinition:ApiableItem, nameOfItemSearched:String, strict:Boolean =false):List<String>{
    return when(instanceOfCollectionItemDefinition){
        is Arme -> getElementAccordingToTypeAsString(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Armure -> getElementAccordingToTypeAsString(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Monster -> getElementAccordingToTypeAsString(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Bouclier -> getElementAccordingToTypeAsString(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Sort -> getElementAccordingToTypeAsString(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Special -> getElementAccordingToTypeAsString(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Joueur -> getElementAccordingToTypeAsString(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Equipe -> getElementAccordingToTypeAsString(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        else-> getElementAccordingToTypeAsString(nameOfItemSearched, instanceOfCollectionItemDefinition as Monster,strict)
    }
}

suspend inline fun <reified T:ApiableItem> getElementAccordingToType(nameOfItemWanted:String, instanceOfCollectionItemDefinition:T, strict:Boolean,):List<T>{
    return if(strict){
        database.getCollection<T>(T::class.simpleName!!).find(ApiableItem::nom eq nameOfItemWanted).toList()
    }else
    {
        val regexp = if(nameOfItemWanted.contains('*')) nameOfItemWanted else ".*$nameOfItemWanted.*"
        database.getCollection<T>(T::class.simpleName!!).find(ApiableItem::nom regex regexp).toList()
    }
}

suspend inline fun <reified T:ApiableItem> getElementAccordingToTypeAsString(nameOfItemWanted:String, instanceOfCollectionItemDefinition:T, strict:Boolean,):List<String>{
    return if(strict){
        database.getCollection<T>(T::class.simpleName!!).find(ApiableItem::nom eq nameOfItemWanted).toList().map { Json.encodeToString(it) }
    }else
    {
        val regexp = if(nameOfItemWanted.contains('*')) nameOfItemWanted else ".*$nameOfItemWanted.*"
        database.getCollection<T>(T::class.simpleName!!).find(ApiableItem::nom regex regexp).toList().map { Json.encodeToString(it) }
    }
}
