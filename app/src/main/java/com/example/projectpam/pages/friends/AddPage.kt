package com.example.projectpam.pages.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projectpam.friend.Friendship
import com.example.projectpam.friend.FriendshipStatus
import com.example.projectpam.user.AppUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPage(navController: NavController? = null, initialQuery: String = "") {
    var searchQuery by remember { mutableStateOf(initialQuery) }
    var searchResult by remember { mutableStateOf<AppUser?>(null) }
    val friendshipStatuses = remember { mutableStateMapOf<String, FriendshipStatus>() }

    // Data dummy untuk pengguna
    val dummyUsers = listOf(
        AppUser(userId = "1", name = "John Doe", username = "johndoe"),
        AppUser(userId = "2", name = "Jane Smith", username = "janesmith"),
        AppUser(userId = "3", name = "Alice Brown", username = "alicebrown"),
        AppUser(userId = "4", name = "Bob White", username = "bobwhite")
    )

    // Fungsi pencarian
    LaunchedEffect(searchQuery) {
        searchResult = dummyUsers.find { user ->
            user.name.contains(searchQuery, ignoreCase = true) ||
                    user.username.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(16.dp)
    ) {
        // Search Bar dan Back Icon Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Bar dengan rounded corners
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    searchResult = dummyUsers.find { user ->
                        user.name.contains(it, ignoreCase = true) ||
                                user.username.contains(it, ignoreCase = true)
                    }
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_search),
                        contentDescription = "Search Icon"
                    )
                },
                placeholder = { Text(text = "Search users to add") },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White, shape = RoundedCornerShape(30.dp)),
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Icon Back untuk navigasi ke halaman "friends"
            IconButton(onClick = { navController?.navigate("friends") }) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_revert),
                    contentDescription = "Back Icon"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Result Section
        if (searchResult != null) {
            val userId = searchResult!!.userId
            // Menemukan status pertemanan menggunakan senderId dan receiverId
            val currentStatus = friendshipStatuses[userId] ?: FriendshipStatus.NONE

            // Menampilkan SearchBox dengan status pertemanan
            SearchBox(
                name = searchResult!!.name,
                friendship = Friendship(
                    id = "friendshipId_${userId}",
                    senderId = "currentUserId", // Gantilah dengan ID pengguna saat ini
                    receiverId = userId,
                    status = currentStatus
                ),
                onStatusChange = { newStatus ->
                    friendshipStatuses[userId] = newStatus
                }
            )
        } else if (searchQuery.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No users found", fontSize = 18.sp, color = Color.Gray)
            }
        }
    }
}
