package configuration

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File


class Configuration() {



    private val PROPERTY_FILE_NAME ="properties.json"

    var properties = AppProperties()

    init {
        val resourcesDir = File(System.getProperty("compose.application.resources.dir"))
        properties = Json.decodeFromString<AppProperties>(resourcesDir.resolve(PROPERTY_FILE_NAME).readText())
    }

    fun getEndpointServer() = "http://${properties.serverUrl}"
}