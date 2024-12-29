package com.example.projectpam.pages.profile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.projectpam.R
import com.example.projectpam.model.AuthState
import com.example.projectpam.model.AuthViewModel
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import com.example.projectpam.model.ProfileViewModel
import com.example.projectpam.user.AppUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


@Composable
fun ProfilePage(
    navController: NavController? = null,
    authViewModel: AuthViewModel? = null
) {
    val context = LocalContext.current

    // Collect authState
    val authState by authViewModel?.authState?.collectAsState(initial = AuthState.Unauthenticated)
        ?: remember { mutableStateOf(AuthState.Unauthenticated) }

    // Extract user data if authenticated
    val user = (authState as? AuthState.Authenticated)?.user

    // Initialize states
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(user?.name.orEmpty()) }
    var username by remember { mutableStateOf(user?.username.orEmpty()) }
    var email by remember { mutableStateOf(user?.email.orEmpty()) }
    var gender by remember { mutableStateOf(user?.gender.orEmpty()) }
    var phoneNumber by remember { mutableStateOf(user?.phoneNumber.orEmpty()) }
    var profilePhotoUrl by remember { mutableStateOf(user?.profilePhotoUrl.orEmpty()) }
    var isUploading by remember { mutableStateOf(false) }
    var contentUri by remember { mutableStateOf<Uri?>(null) }

    // Fungsi untuk memilih gambar dari galeri
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                contentUri = uri  // Menyimpan URI gambar yang dipilih
            } else {
                Toast.makeText(context, "Image selection failed", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Fungsi untuk memilih gambar dari galeri
    fun pickImageFromGallery() {
        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    // Permission launcher for image selection
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
        val isGrantedMedia = permissions[Manifest.permission.READ_MEDIA_IMAGES] == true

        // Cek apakah izin diberikan
        if (isGranted || isGrantedMedia) {
            // Izin diberikan, lanjutkan untuk membuka galeri
            pickImageFromGallery()
        } else {
            // Izin ditolak
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }


    // Function to open gallery

    // Convert image to Base64 and save to Firestore
    fun uploadPhotoToFirestore(uri: Uri) {
        contentUri?.let {
            isUploading = true
            val base64Image = convertImageToBase64(it, context)
            if (base64Image != null) {
                savePhotoMetadataToFirestore(base64Image)
                profilePhotoUrl = base64Image
                isUploading = false
            } else {
                Toast.makeText(context, "Failed to convert image", Toast.LENGTH_SHORT).show()
                isUploading = false
            }
        }
    }

    // UI Components
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Profile Image
            Image(
                painter = rememberAsyncImagePainter(
                    model = when {
                        contentUri != null -> contentUri  // Menampilkan gambar yang dipilih
                        profilePhotoUrl.isNotEmpty() -> profilePhotoUrl // Base64 string langsung
                        else -> R.drawable.default_profile
                    }
                ),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Gray),
                contentScale = ContentScale.Crop
            )

            // Back button
            IconButton(
                onClick = { navController?.navigate("home") },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Loading indicator
            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Change photo button
            if (isEditing) {
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
                        } else {
                            permissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                ) {
                    Text("Change Photo")
                }
            }

            // Save button
            IconButton(
                onClick = {
                    if (isUploading) {
                        Toast.makeText(context, "Please wait until the upload is complete.", Toast.LENGTH_SHORT).show()
                        return@IconButton
                    }
                    if (isEditing) {
                        contentUri?.let { uri ->
                            uploadPhotoToFirestore(uri)
                        }
                    }
                    isEditing = !isEditing
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Filled.Check else Icons.Filled.Edit,
                    contentDescription = if (isEditing) "Save" else "Edit",
                    tint = if (isEditing) Color.Green else Color.White
                )
            }
        }

        // Editable fields for user info
        EditableField(label = "Name :", value = name, isEditing = isEditing) { name = it }
        EditableField(label = "Username :", value = username, isEditing = isEditing) { username = it }
        EditableField(label = "Gender :", value = gender, isEditing = isEditing) { gender = it }
        EditableField(label = "Email :", value = email, isEditing = isEditing) { email = it }
        EditableField(label = "Phone Number :", value = phoneNumber, isEditing = isEditing) { phoneNumber = it }

        // Sign out button
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { authViewModel?.signout() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(
                text = "Sign Out",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}




fun convertImageToBase64(uri: Uri, context: Context): String? {
    return try {
        // Mendapatkan input stream dari URI
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        // Mengonversi Bitmap ke byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        // Mengonversi byte array ke string Base64
        Base64.encodeToString(byteArray, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


fun savePhotoMetadataToFirestore(base64Image: String) {
    val db = FirebaseFirestore.getInstance()

    // Mengambil referensi ke koleksi pengguna
    val userRef = db.collection("users").document("userId") // Gantilah dengan ID pengguna yang benar

    // Menyimpan data Base64
    userRef.update("profilePhotoUrl", base64Image)
        .addOnSuccessListener {
            // Berhasil menyimpan foto
            Log.d("Firestore", "Photo saved successfully")
        }
        .addOnFailureListener { e ->
            // Gagal menyimpan foto
            Log.e("Firestore", "Error saving photo", e)
        }
}






@Preview(showBackground = true)
@Composable
fun ProfilePagePreview() {
    ProfilePage()
}