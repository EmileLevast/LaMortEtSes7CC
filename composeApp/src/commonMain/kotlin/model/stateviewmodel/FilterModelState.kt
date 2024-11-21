package model.stateviewmodel

data class FilterModelState(var filterUser: FilterUser) {
}

enum class FilterUser {
    AUCUN,
    DECOUVERTES,
    EQUIPES,
    ARMES,
    SORTS,
    ARMURES,
    BOUCLIERS,
    SPECIAL
}
