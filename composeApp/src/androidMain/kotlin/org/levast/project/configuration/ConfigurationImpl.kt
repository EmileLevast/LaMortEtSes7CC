package configuration

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.levast.project.configuration.KEY_IP_ADDRESS

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ConfigurationImpl() : IConfiguration {



    private lateinit var properties:AppProperties
    private var context:Context?=null

    override fun getEndpointServer() = "http://${properties.ipAdressServer}:${properties.portServer}"


    init {
        loadAppProperties()
    }

    fun setupContextForPreferences(context: Context){
        this.context=context
    }

    private fun loadAppProperties(){
        runBlocking {
            coroutineScope {
                val exampleCounterFlow: Flow<String> = context?.dataStore?.data?.map { preferences ->
                        preferences[KEY_IP_ADDRESS] ?: "10.0.2.2"
                    } ?: flowOf("10.0.2.2")

                properties.ipAdressServer = exampleCounterFlow
            }
        }
    }

    override fun getIpAdressTargetServer() =  properties.ipAdressServer

    override fun setIpAdressTargetServer(adresseIp: String) {
        properties.ipAdressServer=adresseIp
    }
}