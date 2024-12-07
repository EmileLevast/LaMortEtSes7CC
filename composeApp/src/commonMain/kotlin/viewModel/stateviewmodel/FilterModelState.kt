package viewModel.stateviewmodel

data class FilterModelState(var filterUser: FilterUser) {
}

enum class FilterUser {
    AUCUN,
    TOUT_EQUIPEMENT,
    STATISTIQUES,
    DECOUVERTES,
    EQUIPES,
    ARMES,
    SORTS,
    ARMURES,
    BOUCLIERS,
    SPECIAL
}
