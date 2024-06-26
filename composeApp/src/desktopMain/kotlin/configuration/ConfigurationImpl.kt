package configuration

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import lamortetses7cc.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.io.File


@OptIn(ExperimentalResourceApi::class)
class ConfigurationImpl() : IConfiguration {

    private val PROPERTY_FILE_NAME ="properties.json"

    private lateinit var properties:AppProperties

    override fun getEndpointServer() = "http://${properties.serverUrl}"


    init {
        loadFileProperties()
    }

    fun loadFileProperties(){
        runBlocking {
            coroutineScope {
                properties = AppProperties(Res.readBytes("files/urlserver").decodeToString())
            }
        }
    }

}