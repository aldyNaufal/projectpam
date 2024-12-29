package com.example.projectpam

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.projectpam.model.AuthState
import com.example.projectpam.model.AuthViewModel
import com.example.projectpam.model.PostViewModel
import com.example.projectpam.model.ProfileViewModel
import com.example.projectpam.ui.theme.ProjectpamTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val postViewModel:PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Google Sign In
        authViewModel.initGoogleSignIn(this)

        enableEdgeToEdge()

        setContent {
            ProjectpamTheme {
                // Collect auth state for handling authentication status
                val authState by authViewModel.authState.collectAsState()

                // Handle authentication state changes
                LaunchedEffect(authState) {
                    when (authState) {
                        is AuthState.Error -> {
                            // You can show error messages using Toast or Snackbar
                            Toast.makeText(
                                this@MainActivity,
                                (authState as AuthState.Error).message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> Unit
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigation(
                        modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel,
                        postViewModel = postViewModel
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up when activity is destroyed
        lifecycleScope.launch {
            try {
                authViewModel.signoutGoogle()
            } catch (e: Exception) {
                // Handle any cleanup errors
            }
        }
    }
}