fun String.cleanupForDB():String{
    return this.lowercase().replace("'"," ").replace("é","e").replace("è","e").replace("î","i").replace("ï","i").replace("ä","a").replace("'"," ")
}

fun extractEquipementsListFromJoueur(joueur: Joueur):List<String>{
    return joueur.chaineEquipementSerialisee.extractElements()
}

fun extractDecouvertesListFromEquipe(equipe: Equipe):List<String>{
    return equipe.chaineDecouvertSerialisee.extractElements()
}

private fun String.extractElements():List<String>{
    return this.split("$CHAR_SEP_EQUIPEMENT$CHAR_SEP_EQUIPEMENT").map { it.replace("|", "") }
}