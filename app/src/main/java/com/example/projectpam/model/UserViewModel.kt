package com.example.projectpam.model

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectpam.user.AppUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ViewModel
class UserProfileViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    private val _userState = MutableStateFlow<Result<AppUser>?>(null)
    val userState = _userState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        // Load current user data if authenticated
        auth.currentUser?.let { user ->
            loadUserData(user.uid)
        }
    }

    private fun loadUserData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val documentSnapshot = firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()

                if (documentSnapshot.exists()) {
                    val userData = documentSnapshot.toObject(AppUser::class.java)
                    userData?.let {
                        _userState.value = Result.success(it)
                    }
                }
            } catch (e: Exception) {
                _userState.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(
        name: String,
        username: String,
        gender: String,
        phoneNumber: String,
        photoUri: Uri? = null
    ) {
        val currentUser = auth.currentUser ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Upload photo if provided
                val photoUrl = photoUri?.let { uri ->
                    uploadProfilePhoto(currentUser.uid, uri)
                }

                val userData = hashMapOf<String, Any>(
                    "userId" to currentUser.uid,
                    "name" to name,
                    "username" to username,
                    "email" to (currentUser.email ?: ""),
                    "gender" to gender,
                    "phoneNumber" to phoneNumber,
                    "updatedAt" to FieldValue.serverTimestamp()
                )

                // Add photo URL if uploaded
                photoUrl?.let {
                    userData["profilePhotoUrl"] = it
                }

                // Update Firestore
                firestore.collection("users")
                    .document(currentUser.uid)
                    .update(userData)
                    .await()

                // Reload user data
                loadUserData(currentUser.uid)
            } catch (e: Exception) {
                _userState.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun uploadProfilePhoto(userId: String, photoUri: Uri): String {
        val photoRef = storage.child("profile_photos/$userId.jpg")
        return photoRef.putFile(photoUri).await().storage.downloadUrl.await().toString()
    }

    @SuppressLint("RestrictedApi")
    fun deleteProfile() {
        val currentUser = auth.currentUser ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Delete profile photo if exists
                try {
                    storage.child("profile_photos/${currentUser.uid}.jpg")
                        .delete()
                        .await()
                } catch (e: Exception) {
                    // Ignore if photo doesn't exist
                }

                // Delete user document
                firestore.collection("users")
                    .document(currentUser.uid)
                    .delete()
                    .await()

                // Delete Firebase Auth user
                currentUser.delete().await()

                // Clear user state
                _userState.value = Result.success(AppUser())
            } catch (e: Exception) {
                _userState.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    var user by mutableStateOf(
        AppUser(
            userId = "12345",
            name = "Naura Labibah",
            username = "@nawnaw",
            email = "nauralabib@student.ub.ac.id",
            gender = "Woman",
            phoneNumber = "081223476210",
            profilePhotoUrl = "https://via.placeholder.com/150" // Replace with your image URL
        )
    )

    var isEditing by mutableStateOf(false)

    fun toggleEditMode() {
        isEditing = !isEditing
    }

    fun saveUser(updatedUser: AppUser) {
        user = updatedUser
        isEditing = false
    }
}
