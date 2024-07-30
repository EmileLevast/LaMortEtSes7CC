package viewModel

import IListItem
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import model.AdminModelState

class AdminViewModel : ViewModel(){
    // Game UI state
    private val _uiState = MutableStateFlow(AdminModelState())
    val uiState: StateFlow<AdminModelState> = _uiState.asStateFlow()

    fun addAllToItems(vararg items : IListItem){
        _uiState.value.listitems.addAll(items)
    }

    fun clearAllItems(){
        _uiState.value.listitems.clear()
    }

    fun deleteIf(predicate : (IListItem)->Boolean){
        _uiState.value.listitems.removeAll{item -> predicate(item)}
    }

}