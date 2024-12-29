package com.example.projectpam.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.example.projectpam.user.AppUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import android.util.Base64
import android.util.Log
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await


class ProfileViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Fungsi untuk mengencode gambar menjadi base64
    fun encodeImageToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()

            // Kompres gambar ke format JPEG dengan kualitas 60%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)

            val byteArray = outputStream.toByteArray()
            val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

            // Tambahkan prefix data URL untuk validasi
            "data:image/jpeg;base64,$base64Image"
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error encoding image: ${e.localizedMessage}")
            null
        }
    }

    // Validasi ukuran file dan tipe mime
    fun validateImageFile(context: Context, uri: Uri): Boolean {
        val fileSize = getFileSize(context, uri)
        val mimeType = context.contentResolver.getType(uri)

        return when {
            fileSize > 5 * 1024 * 1024 -> { // Batas ukuran file 5MB
                Log.e("ProfileViewModel", "File size exceeds 5MB.")
                false
            }
            mimeType !in listOf("image/jpeg", "image/png", "image/jpg") -> {
                Log.e("ProfileViewModel", "Invalid file type: $mimeType.")
                false
            }
            else -> true
        }
    }

    // Fungsi untuk mendapatkan ukuran file gambar
    private fun getFileSize(context: Context, uri: Uri): Long {
        return try {
            val fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
            val size = fileDescriptor?.statSize ?: 0
            fileDescriptor?.close()
            size
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error getting file size: ${e.localizedMessage}")
            0L
        }
    }
    suspend fun updateProfilePhoto(context: Context, uri: Uri, user: AppUser): Result<String> {
        return try {
            // Validasi file
            if (!validateImageFile(context, uri)) {
                return Result.failure(Exception("Invalid file size or type"))
            }

            // Encode gambar ke base64
            val base64Image = encodeImageToBase64(context, uri) ?:
            return Result.failure(Exception("Failed to encode image"))

            // Update user document dengan base64 image
            val userUpdate = hashMapOf<String, Any>(
                "profilePhotoUrl" to base64Image,
                "lastUpdated" to FieldValue.serverTimestamp()
            )

            // Update Firestore
            firestore.collection("users")
                .document(user.userId)
                .update(userUpdate)
                .await()

            Result.success(base64Image)
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error updating profile photo: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    // Fungsi untuk mengambil foto profil user
    suspend fun getUserProfilePhoto(userId: String): Result<String> {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            val base64Photo = document.getString("profilePhotoUrl") ?: ""
            Result.success(base64Photo)
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error getting profile photo: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    // Fungsi untuk menghapus foto profil
    suspend fun deleteProfilePhoto(userId: String): Result<Unit> {
        return try {
            val updates = hashMapOf<String, Any>(
                "profilePhotoUrl" to "",
                "lastUpdated" to FieldValue.serverTimestamp()
            )

            firestore.collection("users")
                .document(userId)
                .update(updates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error deleting profile photo: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    // Extension function untuk mengkonversi Base64 ke Bitmap
    fun String.toBitmap(): Bitmap? {
        return try {
            if (this.isEmpty()) return null

            val base64String = if (this.contains(",")) {
                this.split(",")[1]
            } else {
                this
            }

            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            Log.e("ImageUtils", "Error converting base64 to bitmap: ${e.localizedMessage}")
            null
        }
    }

}
