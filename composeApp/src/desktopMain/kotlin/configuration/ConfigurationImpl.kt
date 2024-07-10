package configuration

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class ConfigurationImpl() : IConfiguration {

    private lateinit var properties:AppProperties

    override fun getEndpointServer() = "http://${properties.ipAdressServer}:${properties.portServer}"


    init {
        loadFileProperties()
    }

    private fun loadFileProperties(){
        runBlocking {
            coroutineScope {
                properties = AppProperties(getUrlSharedPreferences())
            }
        }
    }

    override fun setIpAdressTargetServer(adresseIp: String) {
        CoroutineScope(Dispatchers.Default).launch {
            setNewIpAdressToPreferences(adresseIp)
        }

        properties.ipAdressServer = adresseIp

    }

    override fun getIpAdressTargetServer() =  properties.ipAdressServer
}