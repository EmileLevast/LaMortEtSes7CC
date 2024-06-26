package configuration

import kotlinx.serialization.json.Json
import java.io.File


class ConfigurationImpl() : IConfiguration {

    private val PROPERTY_FILE_NAME ="properties.json"

    var properties = AppProperties()

    init {
        val resourcesDir = File(System.getProperty("compose.application.resources.dir"))
        properties = Json.decodeFromString<AppProperties>(resourcesDir.resolve(PROPERTY_FILE_NAME).readText())
    }

    override fun getEndpointServer() = "http://${properties.serverUrl}"
}