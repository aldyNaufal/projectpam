package com.example.projectpam.pages.home


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.projectpam.R


// Import perlu disesuaikan untuk navigasi
import androidx.navigation.NavController

@Composable
fun BottomNavBar(
    selectedScreen: String,
    onScreenSelected: (String) -> Unit,
    navController: NavController // Tambahkan parameter NavController untuk navigasi
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Navigation Bar
        NavigationBar(
            containerColor = Color.White,
            contentColor = Color.Black,
            modifier = Modifier.height(56.dp)
        ) {
            // Home Item
            NavigationBarItem(
                selected = selectedScreen == "home",
                onClick = { onScreenSelected("home") },
                icon = {
                    val icon = if (selectedScreen == "home") {
                        painterResource(id = R.drawable.ic_home_pick)
                    } else {
                        painterResource(id = R.drawable.ic_home_bottom)
                    }
                    Icon(
                        painter = icon,
                        contentDescription = "Home",
                        modifier = Modifier.size(16.dp)
                    )
                }
            )

            // Spacer for Center Button
            Spacer(Modifier.weight(1f))

            // Friends Item
            NavigationBarItem(
                selected = selectedScreen == "friends",
                onClick = { onScreenSelected("friends") },
                icon = {
                    val icon = if (selectedScreen == "friends") {
                        painterResource(id = R.drawable.ic_user_pick)
                    } else {
                        painterResource(id = R.drawable.ic_user)
                    }
                    Icon(
                        painter = icon,
                        contentDescription = "Friends",
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }

        // Center Camera Button
        Box(
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp) // Position above NavigationBar
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFF5C5C), Color(0xFFFFC107))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = {
                navController.navigate("post") // Navigasi ke halaman kamera
            }) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
    // Tambahkan parameter dummy untuk Preview
    BottomNavBar(
        selectedScreen = "home",
        onScreenSelected = {},
        navController = NavController(LocalContext.current) // Dummy NavController
    )
}

