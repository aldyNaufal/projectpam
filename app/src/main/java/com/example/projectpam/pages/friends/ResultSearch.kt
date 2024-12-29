package com.example.projectpam.pages.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projectpam.friend.Friendship
import com.example.projectpam.friend.FriendshipStatus

@Composable
fun SearchBox(
    name: String,
    friendship: Friendship,
    onStatusChange: (FriendshipStatus) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image Placeholder
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Gray, shape = RoundedCornerShape(50))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Name
            Text(text = name, fontSize = 16.sp, modifier = Modifier.weight(1f))

            // Add/Change Button
            Button(
                onClick = {
                    // Jika status pertemanan masih NONE (belum ada permintaan), set status ke PENDING
                    if (friendship.status == FriendshipStatus.PENDING) {
                        return@Button
                    }
                    onStatusChange(FriendshipStatus.PENDING)
                },
                enabled = friendship.status != FriendshipStatus.PENDING // Tombol hanya aktif jika status bukan PENDING
            ) {
                // Menampilkan teks sesuai dengan status
                Text(
                    text = when (friendship.status) {
                        FriendshipStatus.PENDING -> "Pending"
                        else -> "Add" // Jika statusnya belum ada (NONE), tampilkan "Add"
                    }
                )
            }
        }
    }
}

