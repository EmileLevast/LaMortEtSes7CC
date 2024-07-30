package model

import IListItem

data class AdminModelState(var listitems : List<IListItem> = listOf(), var listPinneditems : List<Int> = listOf()) {

}