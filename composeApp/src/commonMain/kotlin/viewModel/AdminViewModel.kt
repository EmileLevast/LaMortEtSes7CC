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

    fun deleteIf(predicate : (IListItem)->Boolean){
        updateListItems(_uiState.value.listitems.filter { item -> !predicate(item) })
    }

    private fun updateListItems(items: List<IListItem>){
        _uiState.update { currentState -> currentState.copy(listitems = items) }
    }

}