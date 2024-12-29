package com.example.projectpam.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectpam.friend.Friendship
import com.example.projectpam.friend.FriendshipStatus
import com.example.projectpam.user.AppUser
import com.example.projectpam.user.dummyUsers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Menambahkan fungsi extension untuk menghasilkan ID unik friendship
private fun generateFriendshipId(senderId: String, receiverId: String): String {
    return "${senderId}_${receiverId}_${System.currentTimeMillis()}"
}

// Data dummy untuk friend requests
val dummyFriendRequests = listOf(
    Friendship(
        id = generateFriendshipId("003", "001"),
        senderId = "003", // Michael Jordan
        receiverId = "001", // Chris Evans
        status = FriendshipStatus.PENDING,
        timestamp = System.currentTimeMillis() - 86400000 // 1 hari yang lalu
    ),
    Friendship(
        id = generateFriendshipId("005", "001"),
        senderId = "005", // Ryan Reynolds
        receiverId = "001", // Chris Evans
        status = FriendshipStatus.PENDING,
        timestamp = System.currentTimeMillis() - 43200000 // 12 jam yang lalu
    ),
    Friendship(
        id = generateFriendshipId("008", "001"),
        senderId = "008", // Jane Smith
        receiverId = "001", // Chris Evans
        status = FriendshipStatus.PENDING,
        timestamp = System.currentTimeMillis() - 3600000 // 1 jam yang lalu
    )
)

// Data dummy untuk existing friendships
val dummyFriendships = listOf(
    Friendship(
        id = generateFriendshipId("001", "002"),
        senderId = "001", // Chris Evans
        receiverId = "002", // Emma Watson
        status = FriendshipStatus.FRIENDS,
        timestamp = System.currentTimeMillis() - 604800000 // 1 minggu yang lalu
    ),
    Friendship(
        id = generateFriendshipId("004", "001"),
        senderId = "004", // Serena Williams
        receiverId = "001", // Chris Evans
        status = FriendshipStatus.FRIENDS,
        timestamp = System.currentTimeMillis() - 1209600000 // 2 minggu yang lalu
    ),
    Friendship(
        id = generateFriendshipId("001", "006"),
        senderId = "001", // Chris Evans
        receiverId = "006", // Scarlett Johansson
        status = FriendshipStatus.FRIENDS,
        timestamp = System.currentTimeMillis() - 2592000000 // 1 bulan yang lalu
    )
)

class FriendViewModel : ViewModel() {
    // StateFlow untuk menyimpan daftar semua user
    private val _users = MutableStateFlow<List<AppUser>>(emptyList())
    val users: StateFlow<List<AppUser>> = _users.asStateFlow()

    // StateFlow untuk menyimpan daftar teman
    private val _friends = MutableStateFlow<List<AppUser>>(emptyList())
    val friends: StateFlow<List<AppUser>> = _friends.asStateFlow()

    // StateFlow untuk menyimpan daftar permintaan pertemanan
    private val _friendRequests = MutableStateFlow<List<Friendship>>(emptyList())
    val friendRequests: StateFlow<List<Friendship>> = _friendRequests.asStateFlow()

    private val _filteredFriends = MutableStateFlow<List<AppUser>>(emptyList())
    val filteredFriends: StateFlow<List<AppUser>> = _filteredFriends
    // LiveData untuk status loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData untuk pesan error
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Menyimpan ID user yang sedang login
    private var currentUserId: String = ""

    init {
        _users.value = dummyUsers
        // Initialize dengan data dummy
        _friendRequests.value = dummyFriendRequests
         _filteredFriends.value = dummyUsers
    }


    // Set current user ID dan muat data terkait
    fun setCurrentUser(userId: String) {
        currentUserId = userId
        loadFriends()
        loadFriendRequests()
    }

    fun filterFriends(query: String) {
        _filteredFriends.value = if (query.isEmpty()) {
            _friends.value
        } else {
            _friends.value.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.username.contains(query, ignoreCase = true)
            }
        }
    }

    // Fungsi untuk memuat daftar teman
    private fun loadFriends() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // Menggunakan data dummy friendships
                val friendIds = dummyFriendships
                    .filter { it.status == FriendshipStatus.FRIENDS }
                    .flatMap { listOf(it.senderId, it.receiverId) }
                    .filter { it != currentUserId }
                    .distinct()

                _friends.value = _users.value.filter { it.userId in friendIds }
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to load friends: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Fungsi untuk memuat permintaan pertemanan
    private fun loadFriendRequests() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // Filter friend requests untuk current user
                _friendRequests.value = dummyFriendRequests.filter {
                    it.receiverId == currentUserId &&
                            it.status == FriendshipStatus.PENDING
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to load friend requests: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Fungsi untuk mencari user berdasarkan username
    fun searchUsers(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                if (query.isEmpty()) {
                    _users.value = dummyUsers
                } else {
                    val searchResults = dummyUsers.filter {
                        it.username.contains(query, ignoreCase = true) &&
                                it.userId != currentUserId
                    }
                    _users.value = searchResults
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to search users: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Fungsi untuk mengirim permintaan pertemanan
    fun sendFriendRequest(targetUserId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val newFriendship = Friendship(
                    id = generateFriendshipId(currentUserId, targetUserId),
                    senderId = currentUserId,
                    receiverId = targetUserId,
                    status = FriendshipStatus.PENDING
                )

                // Update local state untuk simulasi
                _friendRequests.value = _friendRequests.value + newFriendship
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to send friend request: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Fungsi untuk menerima permintaan pertemanan
    fun acceptFriendRequest(friendshipId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Update status di local state untuk simulasi
                val updatedRequests = _friendRequests.value.map { friendship ->
                    if (friendship.id == friendshipId) {
                        friendship.copy(status = FriendshipStatus.FRIENDS)
                    } else {
                        friendship
                    }
                }
                _friendRequests.value = updatedRequests.filter { it.status == FriendshipStatus.PENDING }

                // Reload daftar teman
                loadFriends()
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to accept friend request: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Fungsi untuk menolak permintaan pertemanan
    fun rejectFriendRequest(friendshipId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // Hapus request dari daftar untuk simulasi
                _friendRequests.value = _friendRequests.value.filter { it.id != friendshipId }
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to reject friend request: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Fungsi untuk menghapus teman
    fun removeFriend(friendId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // Update local state untuk simulasi
                val updatedFriends = _friends.value.filter { it.userId != friendId }
                _friends.value = updatedFriends
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to remove friend: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}