package com.example.projectpam.pages.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projectpam.R

@Composable
fun FriendRequestBox(name: String, onAcceptRequest: () -> Unit, onRejectRequest: () -> Unit) {
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

        // Accept Button
        IconButton(onClick = onAcceptRequest) {
            Icon(
                imageVector = Icons.Filled.Check, // Use the Check icon
                contentDescription = "Accept",
                tint = Color.Green
            )
        }

        // Reject Button
        IconButton(onClick = onRejectRequest) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                contentDescription = "Reject",
                tint = Color.Red
            )
        }
    }
}
