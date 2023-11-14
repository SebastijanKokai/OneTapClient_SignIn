package com.example.onetapsignin.auth

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.example.onetapsignin.data.UserData

interface IAuthRepository {
    fun loginWithGoogle(launcher: ActivityResultLauncher<IntentSenderRequest>)
    fun extractUserDataFromIntent(data: Intent?): UserData?
    suspend fun signOut(): Boolean
    fun getSignedInUser(): UserData
}