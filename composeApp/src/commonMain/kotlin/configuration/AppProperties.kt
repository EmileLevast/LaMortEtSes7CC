package configuration

import SERVER_PORT
import kotlinx.serialization.Serializable

@Serializable
class AppProperties(var ipAdressServer:String="", val portServer: Int = SERVER_PORT)