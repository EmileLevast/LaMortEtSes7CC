package viewModel.stateviewmodel

data class FilterModelState(var filterUser: FilterUser) {
}

enum class FilterUser {
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
