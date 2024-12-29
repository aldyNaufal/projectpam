package com.example.projectpam.pages.friends

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FriendsList(friends: List<String>, onRemoveFriend: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        friends.forEach { friend ->
            FriendBox(
                name = friend,
                onRemoveFriend = { onRemoveFriend(friend) }
            )
        }
    }
}
