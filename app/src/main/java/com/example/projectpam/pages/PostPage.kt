package com.example.projectpam.pages

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.projectpam.R
import com.example.projectpam.model.AuthState
import com.example.projectpam.model.AuthViewModel
import com.example.projectpam.model.PostViewModel
import com.example.projectpam.pages.home.BottomNavBar
import com.example.projectpam.pages.home.FriendPost
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Composable
fun PostPage(
    authViewModel: AuthViewModel? = null,
    navController: NavController? = null,
    postViewModel: PostViewModel?= null,
    selectedScreen: String,
    onScreenSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val authState by authViewModel?.authState?.collectAsState() ?: remember { mutableStateOf(AuthState.Unauthenticated) }
    val user = (authState as? AuthState.Authenticated)?.user
    var caption by remember { mutableStateOf("") }

    var isUploading by remember { mutableStateOf(false) }
    var contentUri by remember { mutableStateOf<Uri?>(null) }

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                contentUri = uri
                isUploading = true
                val base64Image = convertImageToBase64(uri, context)
                if (base64Image != null) {
                    savePhotoMetadataToFirestore(base64Image)
                } else {
                    Toast.makeText(context, "Failed to convert image", Toast.LENGTH_SHORT).show()
                }
                isUploading = false
            } else {
                Toast.makeText(context, "Image selection failed", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Scaffold(
        bottomBar = {
            if (navController != null) {
                BottomNavBar(
                    selectedScreen = selectedScreen,
                    onScreenSelected = onScreenSelected,
                    navController = navController
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            // Top Bar with profile image and name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Profile photo
                Image(
                    painter = rememberAsyncImagePainter(user?.profilePhotoUrl),
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))

                // User name
                Text(
                    text = "Hi, ${user?.name ?: "User"}",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
            ) {
                if (contentUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(contentUri),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "No Image Selected",
                        color = Color.Gray,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Icons for delete and download
                // Delete Icon
                IconButton(
                    onClick = { /* Handle delete action */ },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = "Delete",
                        tint = Color.Black
                    )
                }

                // Download Icon
                IconButton(
                    onClick = { /* Handle download action */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.download),
                        contentDescription = "Download",
                        tint = Color.Black
                    )
                }


                // Upload Button
                Button(
                    onClick = { pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                ) {
                    Text(text = if (isUploading) "Uploading..." else "Select Image")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Caption input field
            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Caption") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Post button
            Button(
                onClick = {
                    if (contentUri != null) {
                        val newPost = FriendPost(
                            name = user?.name ?: "User", // Gunakan name bukan username
                            imageUrl = contentUri.toString(),
                            caption = caption,
                            profilePhotoUrl = user?.profilePhotoUrl // Tambahkan foto profil jika tersedia
                        )
                        postViewModel?.addPost(newPost)
                        // Reset state setelah posting
                        contentUri = null
                        caption = ""
                        // Opsional: navigasi kembali ke HomePage
                        navController?.navigate("home")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = "Post")
            }
        }
    }
}

fun convertImageToBase64(uri: Uri, context: Context): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        Base64.encodeToString(byteArray, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun savePhotoMetadataToFirestore(base64Image: String) {
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("users").document("userId") // Replace with correct user ID

    userRef.update("profilePhotoUrl", base64Image)
        .addOnSuccessListener {
            Log.d("Firestore", "Photo saved successfully")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error saving photo", e)
        }
}


@Preview(showBackground = true)
@Composable
fun PostPagePreview() {
    PostPage(
        authViewModel = null,
        navController = null,
        postViewModel = null,
        selectedScreen = "camera",
        onScreenSelected = {}
    )
}

