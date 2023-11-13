package com.example.onetapsignin.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.example.onetapsignin.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes

class AuthRepository constructor(
    context: Context
) : IAuthRepository {
    private val TAG = AuthRepository::class.java.name

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

    override fun handleGoogleActivityResult(result: ActivityResult?) {
        if (result != null) {
            runCatching {
                handleGetCredentialsFromIntent(result.data)
            }.onFailure {
                handleOneTapClientFailure(it)
            }
        }
    }

    private fun handleGetCredentialsFromIntent(data: Intent?) {
        val credential = oneTapClient.getSignInCredentialFromIntent(data)
        val idToken = credential.googleIdToken
        val username = credential.id
        val password = credential.password
        when {
            idToken != null -> {
                Log.d(TAG, "Got ID token: $idToken")
            }

            password != null -> {
                Log.d(TAG, "Got password: $password")
            }

            else -> {
                Log.e(TAG, "No ID token or password!")
            }
        }
    }


    private fun handleOneTapClientFailure(t: Throwable) {
        if (t is ApiException) {
            when (t.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    Log.d(TAG, "One-tap dialog was closed.")
                    // TODO Don't re-prompt the user.
                }

                CommonStatusCodes.NETWORK_ERROR -> {
                    Log.d(TAG, "One-tap encountered a network error.")
                    // TODO Try again or just ignore.
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