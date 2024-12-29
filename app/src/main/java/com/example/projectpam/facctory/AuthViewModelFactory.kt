//package com.example.projectpam.facctory
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.projectpam.model.AuthViewModel
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//
//class AuthViewModelFactory(
//    private val googleSignInClient: GoogleSignInClient
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
//            return AuthViewModel(googleSignInClient) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
