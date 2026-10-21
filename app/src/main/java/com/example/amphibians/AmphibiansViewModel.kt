package com.example.amphibians

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface AmphibiansUiState {
    data class Success(val amphibians: List<Amphibian>) : AmphibiansUiState
    object Error : AmphibiansUiState
    object Loading : AmphibiansUiState
}

class AmphibiansViewModel(
    private val repository: AmphibiansRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AmphibiansUiState>(AmphibiansUiState.Loading)
    val uiState: StateFlow<AmphibiansUiState> = _uiState

    init {
        getAmphibians()
    }

    fun getAmphibians() {
        viewModelScope.launch {
            _uiState.value = AmphibiansUiState.Loading
            try {
                _uiState.value = AmphibiansUiState.Success(repository.getAmphibians())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = AmphibiansUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as AmphibiansApplication
                AmphibiansViewModel(application.container.amphibiansRepository)
            }
        }
    }
}