package configuration


// At the top level of your kotlin file:

class ConfigurationImpl() : IConfiguration {

    private var properties:AppProperties = AppProperties("localhost","franck",9090)

    override fun getEndpointServer() = "http://${properties.ipAdressServer}:${properties.portServer}"

    override fun getIpAdressTargetServer() =  properties.ipAdressServer

    override fun setIpAdressTargetServer(adresseIp: String) {
        properties.ipAdressServer=adresseIp
    }

    override fun setUserName(nomUser: String) {
        properties.userName=nomUser
    }

    override fun getUserName(): String = properties.userName

}