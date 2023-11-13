package com.example.onetapsignin.auth

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest

interface IAuthRepository {
    fun loginWithGoogle(launcher: ActivityResultLauncher<IntentSenderRequest>)
    fun handleGoogleActivityResult(result: ActivityResult?)
}