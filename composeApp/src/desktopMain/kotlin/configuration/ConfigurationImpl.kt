package configuration

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lamortetses7cc.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.io.File


class ConfigurationImpl() : IConfiguration {

    private val PROPERTY_FILE_PATH ="properties"

    private lateinit var properties:AppProperties

    override fun getEndpointServer() = "http://${properties.ipAdressServer}:${properties.portServer}"


    init {
        loadFileProperties()
    }

    private fun loadFileProperties(){
        runBlocking {
            coroutineScope {
                properties = AppProperties(
                    try {
                        File(PROPERTY_FILE_PATH).readText()
                    } catch (e: Exception) {
                        println(e.stackTraceToString())
                        "localhost"
                    }
                )
            }
        }
    }

    override fun getIpAdressTargetServer() =  properties.ipAdressServer

    override fun setIpAdressTargetServer(adresseIp: String) {
        properties.ipAdressServer=adresseIp
        File(PROPERTY_FILE_PATH).writeText(adresseIp)
    }
}