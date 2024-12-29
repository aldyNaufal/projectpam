package com.example.projectpam.pages.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.projectpam.model.AuthState
import com.example.projectpam.model.AuthViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.ui.draw.shadow
import com.example.projectpam.model.PostViewModel

@Composable
fun HomePage(
    authViewModel: AuthViewModel? = null,
    navController: NavController? = null,
    postViewModel: PostViewModel, // Tambahkan parameter PostViewModel
    selectedScreen: String,
    onScreenSelected: (String) -> Unit
) {
    val authState by authViewModel?.authState?.collectAsState() ?: remember { mutableStateOf(AuthState.Unauthenticated) }

    // Ambil posts dari PostViewModel
    val posts by postViewModel.posts.collectAsState()

    when (authState) {
        is AuthState.Authenticated -> {
            val user = (authState as AuthState.Authenticated).user

            Scaffold(
                bottomBar = {
                    // Pastikan parameter `navController` diteruskan dengan benar
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
                            painter = rememberAsyncImagePainter(user.profilePhotoUrl),
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
                            text = "Hi, ${user.name}",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    // Posts from friends
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        // Tampilkan posts yang sudah ada di PostViewModel
                        items(posts) { post ->
                            FriendPostItem(post = post)
                        }
                    }
                }
            }
        }
        else -> {
            // Handle unauthorized access
            LaunchedEffect(Unit) {
                navController?.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }
    }
}



@Composable
fun FriendPostItem(post: FriendPost?) {
    if (post == null) {
        Text(
            text = "Not yet posted",
            modifier = Modifier.padding(16.dp),
            fontSize = 18.sp,
            color = Color.Gray
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp) // Jarak luar komponen
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp)) // Klip sudut komponen utama
    ) {
        // Gambar dan Caption
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Tinggi gambar
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
        ) {
            Image(
                painter = rememberAsyncImagePainter(post.imageUrl),
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Caption di bawah gambar
            Text(
                text = post.caption,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp)) // Jarak antara gambar dan nama + ikon

        // Nama dan Icon Download dalam satu baris
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nama
            Text(
                text = post.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )

            // Ikon download
            IconButton(
                onClick = { downloadImage(post.imageUrl) }
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download",
                    tint = Color.Black
                )
            }
        }
    }
}



fun downloadImage(imageUrl: String) {
    // Simulate image download (replace with actual implementation if needed)
    println("Downloading image from: $imageUrl")
}







data class FriendPost(
    val name: String, // Bukan username
    val imageUrl: String,
    val caption: String,
    val profilePhotoUrl: String? = null, // Opsional
    val timestamp: Long = System.currentTimeMillis()
)

//@Preview(showBackground = true)
//@Composable
//fun HomePagePreview() {
//    HomePage(
//        selectedScreen = "home",
//        onScreenSelected = {},
//    )
//}



