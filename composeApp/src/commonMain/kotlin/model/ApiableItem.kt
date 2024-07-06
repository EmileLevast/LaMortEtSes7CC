import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import network.ApiApp

@Serializable
sealed class ApiableItem() : IListItem {
    @Transient
    val nameForApi = this::class.simpleName

    @Transient
    val namePrecisForApi = "$nameForApi/$ENDPOINT_RECHERCHE_STRICTE"

    @Transient
    val uploadFileForApi = "uploadFile$nameForApi"

    @Transient
    val updateForApi = "update$nameForApi"

    @Transient
    val insertForApi = "insert$nameForApi"

    @Transient
    val deleteForApi = "delete$nameForApi"

    @Transient
    val downloadForApi = "download$nameForApi"

    fun decomposeCSV(sequenceLinesFile: Sequence<String>): List<ApiableItem> {
        val listApiableItem = mutableListOf<ApiableItem>()
        val numberOfSemiColonByLine = sequenceLinesFile.first().count { it == ';' }
        var lineFiltered = sequenceLinesFile.drop(1).toList()

        lineFiltered = lineFiltered.filter { it.isNotBlank() }

        var i = 0
        while (i < lineFiltered.size) {
            var currentLine = lineFiltered[i]

            //recontruire la ligne si elle est ecrite sur plusieurs lignes
            //Soit la ligne actuelle ne compte pas assez de points virgule (y'a un bout de texte qui deborde au milieu de la ligne
            //Soit la ligne suivante ne compte pas de ; alors c'est la description qui deborde)
            while ((i < lineFiltered.size && currentLine.count { it == ';' } < numberOfSemiColonByLine) ||
                (i + 1 < lineFiltered.size && lineFiltered[i + 1].count { it == ';' } == 0)
            ) {
                //alors on cumule avec la ligne suivante
                i++
                currentLine += "\n" + lineFiltered[i]
            }

            val listCSVElementOnLine = currentLine.split(";")

            //If the line is empty we pass it
            if (listCSVElementOnLine.first().isNotBlank()) {
                listApiableItem.add(parseFromCSV(listCSVElementOnLine))
            }

            i++
        }


        return listApiableItem
    }



    fun parseSeuilsForce(inputElement: String): MutableMap<Int, Int> {
        val mapSeuilsForce = mutableMapOf<Int, Int>()

        if (inputElement.isNotEmpty()) {
            inputElement.split("|").forEach { currentForceElement ->
                currentForceElement.split(":").let { currentSeuilForce ->
                    //on check si le type correspond bien a un vrai type
                    mapSeuilsForce[currentSeuilForce.first().toInt()] = currentSeuilForce.last().toInt()
                }
            }
        }

        return mapSeuilsForce
    }

    fun parseDrops(inputElement: String): MutableMap<String, Int> {
        val mapDrops = mutableMapOf<String, Int>()

        if (inputElement.isNotEmpty()) {
            inputElement.split("|").forEach { currentDropElement ->
                currentDropElement.split(":").let { currentSeuildrop ->
                    //on check si le type correspond bien a un vrai type
                    mapDrops[currentSeuildrop.first()] = currentSeuildrop.last().toInt()
                }
            }
        }

        return mapDrops
    }

    /**
     * Convertit une string du format du type :
     * |2/3=>Ph:3
     * |4/5=>Ph:6
     * |6=>Ph:9
     * En une liste d'objets Seuil
     */
    fun parseSeuils(inputElement: String): List<Seuil> {
        val listSeuils = mutableListOf<Seuil>()
        val listValeurUnSeuil = mutableListOf<Int>()
        val valeurTypeAssocies: MutableList<Pair<EffectType, Int>> = mutableListOf()
        if (inputElement.isNotEmpty()) {
            //Si la string n'est pas vide, on supprime les passages à la ligne et on separe chaque niveau de seuil
            inputElement.removeSuffix("\n").split("\n").forEach {
                //puis pour chaque niveau de seuil on sépare au niveau du =>
                val listSeuilsParFacteur = it.split("=>")
                listSeuilsParFacteur.first().removePrefix("|").let { itInutilise ->
                    //on regarde l'element de gauche du split, le niveau des seuils
                    itInutilise.split("/").forEach { itSeuils ->
                        //on separe chaque niveau de seuil selon "/" et on les ajoute à la liste de seuils
                        listValeurUnSeuil.add(itSeuils.toInt())
                    }

                }
                //on parse maintenant le type de la valeur du seuil associé
                listSeuilsParFacteur.last().let { itValeursSelonTypes ->
                    itValeursSelonTypes.split("|").forEach {itValeurSelonType ->
                        //pour chaque type du seuil, on split pour recuperer le type d'un cote et la valeur de l'autre
                        val typeValeur = itValeurSelonType.split(":")
                        valeurTypeAssocies.add(Pair(parseEffectType(typeValeur.first()),typeValeur.last().toInt()))
                    }

                }

                //on ajoute le seuil ainsi créé à la liste
                listSeuils.add(Seuil(listValeurUnSeuil,valeurTypeAssocies))
                valeurTypeAssocies.clear()
                listValeurUnSeuil.clear()

            }
        }
        return listSeuils
    }

    fun parseSpellType(inputElement: String): SpellType {
        return SpellType.entries.find { it.name == inputElement } ?: SpellType.AME
    }

    fun parseEffectType(inputElement: String): EffectType {
        return EffectType.entries.find { it.shortname == inputElement } ?: EffectType.PHYSICAL
    }

    fun parseSpecialItemType(inputElement: String): SpecialItemType {
        return SpecialItemType.entries.find { it.name == inputElement } ?: SpecialItemType.OUTIL
    }

    fun deparseSeuils(seuils: Map<String, List<Int>>): String {
        var res = ""
        seuils.forEach { seuil ->
            seuil.value.forEach {
                res += "$it/"
            }
            res = res.removeSuffix("/")
            res += "=" + seuil.key + "|"
        }

        return res.removeSuffix("|")
    }



    fun deparseForce(defense: Map<Int, Int>): String {
        var res = ""
        defense.forEach {
            res += it.key.toString() + ":" + it.value + "|"
        }
        return res.removeSuffix("|")
    }

    fun deparseListDrops(drops: Map<String, Int>): String {
        var res = ""
        drops.forEach {
            res += it.key + ":" + it.value + "|"
        }
        return res.removeSuffix("|")
    }

    abstract fun parseFromCSV(listCSVElement: List<String>): ApiableItem

    override fun getStatsSimplifiedAsStrings(): String {
        return getStatsAsStrings()
    }

    /**
     * Retourne une string qui met en forme les degats des coups critiques en multipliant le facteur par les degats de l'arme
     */
    protected fun computeCoupCritiqueToStringSimplifie(ccStr: String, parseDegat: Map<EffectType, Int>): String {

        //on sépare en deux d'un côté le seuil spécial du coup critique et de l'autre le facteur de ce seuil
        val ccSplit = if (ccStr.contains("=>x")) {
            ccStr.split("=>x")
        } else {
            ccStr.split("=>×")
        }

        val degatFinaux = parseDegat.mapValues { degatSeuil ->
            degatSeuil.value * try {
                ccSplit.last().toInt()
            } catch (e: Exception) {
                -1
            }
        }
        return ccSplit.first() + "=>" + degatFinaux.map { type -> type.key.shortname + ":" + type.value }
            .joinToString("|")
    }

    /**
     * En sortie Pair<String,String> le premier élément correspond au textes des seuils, le deuxième au texte des coups critiques
     */
    protected fun simplificationTextesSeuilsEtCc(
        typeValeurAssociee: String,
        mapSeuilsFacteurAssocie: Map<String, List<Int>>,
        coupsCritiquesInput: String
    ): Pair<String, String> {

        //on recupere la string avec la valeur de defense ou de degats de base selon le type
        val stringTypeValeurCorrigee = parseDefense(typeValeurAssociee.replace("P:", "Ph:"))

        return simplificationTextesSeuilsEtCCPourMap(
            stringTypeValeurCorrigee,
            mapSeuilsFacteurAssocie,
            coupsCritiquesInput
        )
    }

    /**
     * En sortie Pair<String,String> le premier élément correspond au textes des seuils, le deuxième au texte des coups critiques
     */
    protected fun simplificationTextesSeuilsEtCCPourMap(
        mapTypeValeurAssocie: Map<EffectType, String>,
        mapSeuilsFacteurAssocie: Map<String, List<Int>>,
        coupsCritiquesInput: String
    ): Pair<String, String> {
        //dans cet Map on possède en clé le type et en valeur un entier qui correspond au degat de base
        var textSeuils = ""
        val parseValeurTypeInital = mapTypeValeurAssocie.mapValues {
            try {
                it.value.toInt()
            } catch (e: Exception) {
                -1
            }
        }


        var facteur: Int
        var degatFinaux: Map<EffectType, Int>
        //ici on va multiplier chaque valeur des seuils par la valeur des types initiaux
        mapSeuilsFacteurAssocie.forEach {
            facteur = try {
                it.key.toInt()
            } catch (e: Exception) {
                -1
            }


            degatFinaux =
                parseValeurTypeInital.mapValues { valeurSelonSeuil -> valeurSelonSeuil.value * facteur }

            //dans cette liste chaque élément correspond à un seuil avec en valeur la multiplication entre le resultat du seuil et les valeurs de bases
            val listDegatFinaux = degatFinaux.map { type -> type.key.shortname + ":" + type.value }

            //Ici c'est une string avec tous les seuils de la liste précédente afficher sur plusieurs lignes
            textSeuils += "|${it.value.joinToString("/")}=>${listDegatFinaux.joinToString("|")}\n"

        }

        var coupcCritiquesCalcules = strSimplify(coupsCritiquesInput, true)
        if (coupcCritiquesCalcules.isNotBlank() && coupcCritiquesCalcules.first().isDigit()) {

            if (coupcCritiquesCalcules.contains("|")) {
                val tempSplit = coupcCritiquesCalcules.split("|")
                coupcCritiquesCalcules = ""
                tempSplit.forEach {
                    coupcCritiquesCalcules += computeCoupCritiqueToStringSimplifie(
                        it,
                        parseValeurTypeInital
                    ) + "|"
                }
            } else {
                coupcCritiquesCalcules = computeCoupCritiqueToStringSimplifie(
                    coupcCritiquesCalcules,
                    parseValeurTypeInital
                )
            }
        }
        return Pair(textSeuils, coupcCritiquesCalcules)
    }

    override fun getBackgroundBorder(): String = "border$nameForApi.svg"

    final override fun getImage(apiApp: ApiApp): ImageBitmap? {
        var image = mapImagesDownload[imageName]
        //si on a pas trouvé l'image dans les images téléchargées
        if(image == null){
            image = apiApp.downloadImageWithName(imageName).apply { mapImagesDownload[imageName] = this }
        }

        return image
    }

}