package com.example.onetapsignin.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.example.onetapsignin.R
import com.example.onetapsignin.data.UserData
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepository constructor(
    context: Context
) : IAuthRepository {
    private val TAG = AuthRepository::class.java.name

    // Should be stored in DB, but this project is only about logging in/out and showing profile data
    private var signedInUser: UserData = UserData()

    private val oneTapClient: SignInClient = Identity.getSignInClient(context)
    private val signInRequest: BeginSignInRequest = BeginSignInRequest.builder()
        .setPasswordRequestOptions(
            BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build()
        )
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(context.getString(R.string.server_client_id))
                .setFilterByAuthorizedAccounts(true)
                .build()
        )
        .setAutoSelectEnabled(false)
        .build()

    override fun loginWithGoogle(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                runCatching {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    launcher.launch(intentSenderRequest)
                }.onFailure {
                    Log.e(TAG, it.localizedMessage)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, e.localizedMessage)
            }
    }

    override fun extractUserDataFromIntent(data: Intent?): UserData? {
        val credential = oneTapClient.getSignInCredentialFromIntent(data)
        credential.googleIdToken ?: return null

        val user = UserData(
            id = credential.id,
            displayName = credential.displayName,
            firstName = credential.givenName,
            lastName = credential.familyName,
            pictureUrl = credential.profilePictureUri?.toString()
        )

        if (user != null) {
            signedInUser = user
        }

        return user
    }

    override suspend fun signOut(): Boolean = suspendCancellableCoroutine { continuation ->
        oneTapClient.signOut()
            .addOnCompleteListener {
                signedInUser = UserData()
                continuation.resume(it.isSuccessful)
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
            .addOnCanceledListener {
                continuation.cancel()
            }
    }

    override fun getSignedInUser(): UserData = signedInUser

    private fun handleOneTapClientFailure(t: Throwable) {
        if (t is ApiException) {
            when (t.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    Log.d(TAG, "One-tap dialog was closed.")
                }

                CommonStatusCodes.NETWORK_ERROR -> {
                    Log.d(TAG, "One-tap encountered a network error.")
                }

                else -> {
                    Log.d(
                        TAG, "Couldn't get credential from result." +
                                " (${t.localizedMessage})"
                    )
                }
            }
        } else {
            Log.e(TAG, t.localizedMessage)
        }
    }

}