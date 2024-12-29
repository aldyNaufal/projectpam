package com.example.projectpam.pages.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.projectpam.pages.home.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendListPage(selectedScreen: String, onScreenSelected: (String) -> Unit, navController: NavController) {
    var friends by remember { mutableStateOf(listOf("Chris Evans", "Rubens", "Owen", "Alifa")) }
    var friendRequests by remember { mutableStateOf(listOf("Nadia", "Sam", "Jessica")) } // Dummy Data
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedScreen = selectedScreen,
                onScreenSelected = onScreenSelected,
                navController = navController
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .background(Color(0xFFF9F9F9))
                .padding(16.dp)
        ) {
            // Search Bar dan Add Icon Row
            Row(
                modifier = Modifier
                 .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search Bar dengan rounded corners
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                        navController.navigate("search_page/$query")
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_search),
                            contentDescription = "Search Icon"
                        )
                    },
                    placeholder = { Text(text = "Search friends") },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, shape = RoundedCornerShape(30.dp))
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Add Icon
                IconButton(
                    onClick = {
                        navController.navigate("add")
                    },
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(30.dp))
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Friend",
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Judul Friends
            Text(
                text = "Friends:",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Cek apakah daftar teman kosong
            if (friends.isEmpty()) {
                Text(
                    "No friends found.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            } else {
                // Tampilkan daftar teman
                FriendsList(
                    friends = friends,
                    onRemoveFriend = { friend ->
                        friends = friends - friend
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Friend Request Section
            Text(
                text = "Friend Requests:",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (friendRequests.isEmpty()) {
                Text(
                    "No friend requests available.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            } else {
                // Tampilkan daftar permintaan pertemanan
                friendRequests.forEach { request ->
                    FriendRequestBox(
                        name = request,
                        onAcceptRequest = {
                            friends = friends + request
                            friendRequests = friendRequests - request
                        },
                        onRejectRequest = {
                            friendRequests = friendRequests - request
                        }
                    )
                }
            }
        }
    }
}







//@Preview(showBackground = true)
//@Composable
//fun FriendListPagePreview() {
//    FriendListPage()
//}