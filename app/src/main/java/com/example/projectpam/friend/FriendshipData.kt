package com.example.projectpam.friend

enum class FriendshipStatus {
    PENDING,
    FRIENDS,
    NONE
}

// Data class untuk menyimpan data pertemanan
data class Friendship(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val status: FriendshipStatus = FriendshipStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis(),
)