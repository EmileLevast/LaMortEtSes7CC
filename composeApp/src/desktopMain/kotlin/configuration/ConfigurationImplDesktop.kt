package configuration

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.io.File


class ConfigurationImplDesktop() : IConfiguration {

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

    /*
    Sur Desktop y'a pas d'utilisateur associé à l'application donc on laisse vide ces fonctions
     */
    override fun setUserName(nomUser: String) {
    }

    override fun getUserName(): String =""
}