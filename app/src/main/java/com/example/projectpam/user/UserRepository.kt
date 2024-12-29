package com.example.projectpam.user

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage.reference
    private val usersCollection = firestore.collection("users")

    // Create/Update user profile
    suspend fun updateUserProfile(
        userId: String,
        userData: Map<String, Any>,
        photoUri: Uri? = null
    ): Result<AppUser> = try {
        // If photo is provided, upload it first
        val photoUrl = photoUri?.let { uri ->
            uploadProfilePhoto(userId, uri)
        }

        // Add photo URL to userData if available
        val finalUserData = if (photoUrl != null) {
            userData + ("profilePhotoUrl" to photoUrl)
        } else {
            userData
        }

        // Add timestamp
        val dataWithTimestamp = finalUserData + mapOf(
            "updatedAt" to FieldValue.serverTimestamp()
        )

        // Update Firestore
        usersCollection.document(userId)
            .set(dataWithTimestamp, SetOptions.merge())
            .await()

        // Fetch and return updated user data
        val updatedUser = getUserById(userId)
        Result.success(updatedUser)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Upload profile photo
    private suspend fun uploadProfilePhoto(userId: String, photoUri: Uri): String {
        val photoRef = storage.child("profile_photos/$userId.jpg")
        return photoRef.putFile(photoUri).await().storage.downloadUrl.await().toString()
    }

    // Get user by ID
    suspend fun getUserById(userId: String): AppUser {
        val document = usersCollection.document(userId).get().await()
        return document.toObject<AppUser>() ?: throw IllegalStateException("User not found")
    }

    // Get user by username
    suspend fun getUserByUsername(username: String): AppUser? {
        val query = usersCollection.whereEqualTo("username", username).get().await()
        return query.documents.firstOrNull()?.toObject<AppUser>()
    }

    // Delete user
    suspend fun deleteUser(userId: String): Result<Unit> = try {
        // Delete profile photo if exists
        try {
            storage.child("profile_photos/$userId.jpg").delete().await()
        } catch (e: Exception) {
            // Ignore if photo doesn't exist
        }

        // Delete user document
        usersCollection.document(userId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Update specific fields
    suspend fun updateUserFields(userId: String, updates: Map<String, Any>): Result<Unit> = try {
        val updatesWithTimestamp = updates + mapOf(
            "updatedAt" to FieldValue.serverTimestamp()
        )
        usersCollection.document(userId).update(updatesWithTimestamp).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}