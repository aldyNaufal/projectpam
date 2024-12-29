package com.example.projectpam.model

import androidx.lifecycle.ViewModel
import com.example.projectpam.pages.home.FriendPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PostViewModel : ViewModel() {
    private val _posts = MutableStateFlow<List<FriendPost>>(emptyList())
    val posts: StateFlow<List<FriendPost>> = _posts.asStateFlow()

    fun addPost(post: FriendPost) {
        _posts.value = _posts.value + post
    }
}