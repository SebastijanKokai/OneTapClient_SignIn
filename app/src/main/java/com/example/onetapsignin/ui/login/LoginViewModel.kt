package com.example.onetapsignin.ui.login

import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.onetapsignin.auth.IAuthRepository

class LoginViewModel constructor(private val authRepository: IAuthRepository) : ViewModel() {
    fun handleGoogleActivityResult(result: ActivityResult) {
        authRepository.handleGoogleActivityResult(result)
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