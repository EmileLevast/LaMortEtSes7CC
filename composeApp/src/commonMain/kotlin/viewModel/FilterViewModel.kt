package viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import viewModel.stateviewmodel.FilterModelState
import viewModel.stateviewmodel.FilterUser

class FilterViewModel: ViewModel() {
    // Filter UI state
    private val _uiState = MutableStateFlow(FilterModelState(FilterUser.TOUT_EQUIPEMENT))
    val uiState: StateFlow<FilterModelState> = _uiState.asStateFlow()

    fun changeFilterUser(filterSelected : FilterUser){
        _uiState.value = FilterModelState(filterSelected)
    }
}