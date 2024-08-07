package viewModel

import IListItem
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import model.AdminModelState

class AdminViewModel : ViewModel(){
    // Game UI state
    private val _uiState = MutableStateFlow(AdminModelState())
    val uiState: StateFlow<AdminModelState> = _uiState.asStateFlow()

    fun addAllToItems(vararg items : IListItem){
        updateListItems(_uiState.value.listitems+items)
    }

    fun keepPinnedItemsOnly(){
        updateListItems(_uiState.value.listitems.filter { item -> _uiState.value.listPinneditems.contains(item.nom) })
    }

    fun pinItem(itemNom:String){
        updateListPinnedItems(_uiState.value.listPinneditems+itemNom)
    }

    fun removePin(itemToRemove:String){
        updateListPinnedItems(_uiState.value.listPinneditems.filter { itemNom -> itemNom != itemToRemove })
    }

    private fun updateListItems(items: List<IListItem>){
        _uiState.update { currentState -> currentState.copy(listitems = items) }
    }

    private fun updateListPinnedItems(items: List<String>){
        _uiState.update { currentState -> currentState.copy(listPinneditems = items) }
    }

}