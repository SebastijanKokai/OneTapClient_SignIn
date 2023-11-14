package com.example.onetapsignin.auth

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.example.onetapsignin.data.UserData
import com.example.onetapsignin.ui.login.LoginUiState

interface IAuthRepository {
    fun loginWithGoogle(launcher: ActivityResultLauncher<IntentSenderRequest>)
    fun extractUserDataFromIntent(data: Intent?): UserData?
}