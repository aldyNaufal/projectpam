package com.example.projectpam.model

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectpam.user.AppUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class AuthViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private lateinit var googleAuthClient: GoogleAuthClient

    init {
        checkCurrentUser()
    }

    fun String.toBitmap(): Bitmap? {
        return try {
            if (this.isEmpty()) return null

            // Menghapus prefix "data:image/*;base64,"
            val base64String = if (this.contains(",")) {
                this.split(",")[1]
            } else {
                this
            }

            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            Log.e("toBitmap", "Error converting base64 to bitmap: ${e.localizedMessage}")
            null
        }
    }


    private fun checkCurrentUser() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            fetchUserData(currentUser.uid)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let { fetchUserData(it.uid) }
                } else {
                    _authState.value = AuthState.Error(
                        task.exception?.localizedMessage ?: "Login failed"
                    )
                }
            }
    }

    fun signup(
        name: String,
        username: String,
        email: String,
        password: String
    ) {
        _authState.value = AuthState.Loading

        // Validasi input
        if (!isInputValid(name, username, email, password)) return

        // Cek ketersediaan username
        firestore.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { usernameCheck ->
                if (usernameCheck.isEmpty) {
                    createUserAccount(name, username, email, password)
                } else {
                    _authState.value = AuthState.Error("Username sudah digunakan")
                }
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(
                    e.localizedMessage ?: "Gagal memeriksa username"
                )
            }
    }

    private fun isInputValid(
        name: String,
        username: String,
        email: String,
        password: String
    ): Boolean {
        return when {
            name.isBlank() -> {
                _authState.value = AuthState.Error("Nama tidak boleh kosong")
                false
            }

            username.isBlank() -> {
                _authState.value = AuthState.Error("Username tidak boleh kosong")
                false
            }

            email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _authState.value = AuthState.Error("Email tidak valid")
                false
            }

            password.length < 6 -> {
                _authState.value = AuthState.Error("Password minimal 6 karakter")
                false
            }

            else -> true
        }
    }

    private fun createUserAccount(
        name: String,
        username: String,
        email: String,
        password: String
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: return@addOnCompleteListener
                    saveUserToFirestore(userId, name, username, email)
                } else {
                    _authState.value = AuthState.Error(
                        task.exception?.localizedMessage ?: "Pendaftaran gagal"
                    )
                }
            }
    }


    private fun fetchUserData(userId: String) {
        _authState.value = AuthState.Loading
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = AppUser(
                        userId = userId,
                        name = document.getString("name") ?: "",
                        username = document.getString("username") ?: "",
                        email = document.getString("email") ?: "",
                        gender = document.getString("gender") ?: "",
                        phoneNumber = document.getString("phoneNumber") ?: "",
                        profilePhotoUrl = document.getString("profilePhotoUrl") ?: ""
                    )
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    // If user doesn't exist in Firestore, create a new user document
                    val firebaseUser = firebaseAuth.currentUser
                    firebaseUser?.let { fbUser ->
                        val userMap = mapOf(
                            "userId" to fbUser.uid,
                            "name" to (fbUser.displayName ?: ""),
                            "email" to (fbUser.email ?: ""),
                            "username" to (fbUser.email?.split("@")?.get(0) ?: ""),
                            "gender" to "",
                            "phoneNumber" to "",
                            "profilePhotoUrl" to (fbUser.photoUrl?.toString() ?: "")
                        )

                        firestore.collection("users").document(fbUser.uid)
                            .set(userMap)
                            .addOnSuccessListener {
                                fetchUserData(fbUser.uid)
                            }
                            .addOnFailureListener { exception ->
                                _authState.value = AuthState.Error(
                                    "Gagal menyimpan data: ${exception.localizedMessage}"
                                )
                            }
                    } ?: run {
                        _authState.value = AuthState.Error("User data not found")
                    }
                }
            }
            .addOnFailureListener { e ->
                _authState.value =
                    AuthState.Error(e.localizedMessage ?: "Failed to fetch user data")
            }
    }

    private fun saveUserToFirestore(
        userId: String,
        name: String,
        username: String,
        email: String
    ) {
        val userMap = mapOf(
            "userId" to userId,
            "name" to name,
            "username" to username,
            "email" to email,
            "gender" to "",
            "phoneNumber" to "",
            "profilePhotoUrl" to ""
        )

        firestore.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                fetchUserData(userId)
            }
            .addOnFailureListener { exception ->
                _authState.value = AuthState.Error(
                    "Gagal menyimpan data: ${exception.localizedMessage}"
                )
            }
    }

    fun signout() {
        firebaseAuth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    // Tambahkan fungsi untuk inisialisasi Google Sign In
    fun initGoogleSignIn(context: Context) {
        googleAuthClient = GoogleAuthClient(context) // Initialize with the context
    }

    // Fungsi untuk mendapatkan intent Google Sign In
    fun getGoogleSignInIntent(): Intent {
        return googleAuthClient.getSignInIntent()
    }

    // Fungsi untuk menangani hasil Google Sign In
    suspend fun signInWithGoogle() {
        _authState.value = AuthState.Loading

        try {
            val signInSuccess = googleAuthClient.signin()
            if (signInSuccess) {
                val user = firebaseAuth.currentUser
                user?.let {
                    // Cek apakah user sudah ada di Firestore
                    fetchUserData(it.uid)
                } ?: run {
                    _authState.value = AuthState.Error("User not found after Google Sign In")
                }
            } else {
                _authState.value = AuthState.Error("Google Sign In failed")
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(
                e.localizedMessage ?: "Google Sign In failed"
            )
        }
    }

    suspend fun signoutGoogle() {
        googleAuthClient.signout() // Use the custom signout method
        _authState.value = AuthState.Unauthenticated
    }

    // In ViewModel
    fun updateProfile(
        name: String,
        gender: String,
        phoneNumber: String,
        profilePhotoUrl: String? = null,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userUpdates = mutableMapOf<String, Any>(
                "name" to name,
                "gender" to gender,
                "phoneNumber" to phoneNumber
            )

            // Jika ada URL foto profil dalam format base64, tambahkan ke pembaruan
            profilePhotoUrl?.let {
                userUpdates["profilePhotoUrl"] = it
            }

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .update(userUpdates)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }
        }
    }

    fun updateProfileWithPhotoUrl(
        context: Context,
        uri: Uri,
        name: String,
        gender: String,
        phoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Encode gambar menjadi base64
        val profileViewModel = ProfileViewModel()
        val base64Image = profileViewModel.encodeImageToBase64(context, uri)

        if (base64Image != null) {
            // Jika encode berhasil, perbarui profil dengan URL foto base64
            updateProfile(name, gender, phoneNumber, base64Image, onSuccess, onFailure)
        } else {
            // Jika gagal mengencode gambar, beri feedback error
            onFailure(Exception("Failed to encode image"))
        }
    }
}

// State autentikasi
sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: AppUser) : AuthState()
    data class Error(val message: String) : AuthState()
    data class Uploading(val progress: Int) : AuthState() // Progress bar
}


