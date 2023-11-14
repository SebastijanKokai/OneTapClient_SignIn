package com.example.onetapsignin.ui.login

import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.onetapsignin.auth.IAuthRepository
import com.example.onetapsignin.data.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel constructor(private val authRepository: IAuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun handleGoogleActivityResult(result: ActivityResult) {
        runCatching {
            authRepository.extractUserDataFromIntent(result.data)
        }.onSuccess {
            if (it == null) {
                _uiState.value = LoginUiState.Error(Throwable("No user data"))
            } else {
                _uiState.value = LoginUiState.Success(it)
            }
        }.onFailure {
            _uiState.value = LoginUiState.Error(it)
        }
    }

    fun googleLogin(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        authRepository.loginWithGoogle(launcher)
    }
}

class LoginViewModelFactory(private val repository: IAuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class LoginUiState {
    object Initial : LoginUiState()
    data class Success(val userData: UserData) : LoginUiState()
    data class Error(val exception: Throwable) : LoginUiState()
}