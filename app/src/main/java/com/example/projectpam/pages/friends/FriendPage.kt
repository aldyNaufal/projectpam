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


@Composable
fun FriendBox(name: String, onRemoveFriend: () -> Unit) {
    var isMenuVisible by remember { mutableStateOf(false) }

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

            // Three dots menu using Text
            Text(
                text = ". . .",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                modifier = Modifier.clickable { isMenuVisible = true }
            )
        }

        // Popup Box
        if (isMenuVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { isMenuVisible = false } // Dismiss popup when clicking outside
                    .padding(end = 16.dp, top = 8.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Remove Friend",
                        color = Color.Red,
                        modifier = Modifier
                            .clickable {
                                onRemoveFriend()
                                isMenuVisible = false
                            }
                            .padding(8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}