package viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import model.stateviewmodel.FilterUser
import model.stateviewmodel.FilterModelState

class FilterViewModel: ViewModel() {
    // Filter UI state
    private val _uiState = MutableStateFlow(FilterModelState(FilterUser.AUCUN))
    val uiState: StateFlow<FilterModelState> = _uiState.asStateFlow()

    fun changeFilterUser(filterSelected : FilterUser){
        _uiState.value = FilterModelState(filterSelected)
    }
}