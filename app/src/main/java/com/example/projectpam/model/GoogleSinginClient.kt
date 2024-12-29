package com.example.projectpam.model

import androidx.credentials.CredentialManager
import android.content.Context
import android.content.Intent
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.example.projectpam.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await


class GoogleAuthClient(private val context: Context) {

    private val tag = "GoogleSignInClient: "
    private val credential = CredentialManager.create(context)
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun isSignin(): Boolean {
        if (firebaseAuth.currentUser != null) {
            println(tag + "already signed in")
            return true
        }
        return false
    }

    suspend fun signin(): Boolean {
        if (isSignin()) {
            return true
        }

        try {
            val result = getCredentialRequest()
            return handleSignin(result)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e

            println(tag + "Signin error: ${e.message}")
            return false
        }
    }

    private suspend fun getCredentialRequest(): GetCredentialResponse {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(
                        "121755246090-0k810v2nrius97hk3dhcokjlmbs060tg.apps.googleusercontent.com"
                    )
                    .setAutoSelectEnabled(false)
                    .build()
            )
            .build()
        return credential.getCredential(
            request = request,
            context = context
        )
    }

    private suspend fun handleSignin(result: GetCredentialResponse): Boolean {
        val credential = result.credential

        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                println(tag + "name: ${tokenCredential.displayName}")
                println(tag + "email: ${tokenCredential.id}")
                println(tag + "image: ${tokenCredential.profilePictureUri}")

                val authCredential = GoogleAuthProvider.getCredential(
                    tokenCredential.idToken, null
                )

                val authResult = firebaseAuth.signInWithCredential(authCredential).await()

                return authResult.user != null

            } catch (e: GoogleIdTokenParsingException) {
                println(tag + "GoogleIdTokenParsingException: ${e.message}")
                return false
            }
        } else {
            println(tag + "credential is not GoogleIdTokenCredential")
            return false
        }
    }

    suspend fun signout() {
        credential.clearCredentialState(ClearCredentialStateRequest())
        firebaseAuth.signOut()
    }

    fun getSignInIntent(): Intent {
        // Gunakan library Google untuk mendapatkan intent
        val googleSignInClient = GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.client_id))
                .requestEmail()
                .build()
        )
        return googleSignInClient.signInIntent
    }


}
