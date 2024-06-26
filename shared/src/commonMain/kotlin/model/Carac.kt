import kotlinx.serialization.Serializable

@Serializable
class Carac(
    var vie: Int=0,
    var force: Int=0,
    var defense: Map<EffectType, String> = mapOf(),
    var intelligence: Int=0,
    var energie: Int=0,
    var humanite: Int=0,
    var ame: Int=0,
) {

    // Copy constructor
    constructor(other: Carac) : this(
        vie = other.vie,
        force = other.force,
        defense = other.defense.toMap(),  // Creating a new instance of Map to avoid sharing references
        intelligence = other.intelligence,
        energie = other.energie,
        humanite = other.humanite,
        ame = other.ame
    )

    fun toCSV():String{
        return "$vie/$force/${deparseDefense(defense)}/$intelligence/$energie/$humanite/$ame"
    }

    fun showWithComparisonOriginCarac(originCarac:Carac):String{
        return "Vie (${originCarac.vie}): $vie\n" +
                "Force (${originCarac.force}): $force\n" +
                "Defense : ${convertEffectTypeStatsToString(originCarac.defense)}\n" +
                "Intelligence (${originCarac.intelligence}): $intelligence\n" +
                "Energie (${originCarac.energie}): $energie\n" +
                "Humanite : $humanite\n" +
                "Ames : $ame\n"
    }

    companion object {
        fun fromCSV(csvStr:String):Carac{
            val listCarac = csvStr.split("/")
            return Carac(
                listCarac[0].getIntOrZero(),
                listCarac[1].getIntOrZero(),
                parseDefense(listCarac[2]),
                listCarac[3].getIntOrZero(),
                listCarac[4].getIntOrZero(),
                listCarac[5].getIntOrZero(),
                listCarac[6].getIntOrZero(),
            )
        }
    }
}