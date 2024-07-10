package configuration

interface IConfiguration {
    fun getEndpointServer(): String
    fun setIpAdressTargetServer(adresseIp:String):Unit
    fun getIpAdressTargetServer():String
}