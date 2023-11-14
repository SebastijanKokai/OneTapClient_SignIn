package com.example.onetapsignin.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.onetapsignin.auth.IAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel constructor(private val authRepository: IAuthRepository) : ViewModel() {

    private val _logoutState = MutableStateFlow(LogoutState())
    val logoutState: StateFlow<LogoutState> = _logoutState.asStateFlow()

    fun signOut() {
        viewModelScope.launch {
            runCatching {
                val result = authRepository.signOut()
                _logoutState.value = LogoutState(result, "")
            }.onFailure {
                _logoutState.value = LogoutState(false, it.localizedMessage)
            }
        }
    }
}

class HomeViewModelFactory(private val repository: IAuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class LogoutState(
    val isSuccessful: Boolean = false,
    val errorMessage: String = ""
)