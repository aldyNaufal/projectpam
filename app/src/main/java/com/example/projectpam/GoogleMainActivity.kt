package com.example.projectpam

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.projectpam.model.GoogleAuthClient
import com.example.projectpam.ui.theme.ProjectpamTheme
import kotlinx.coroutines.launch

//class MainActivity : ComponentActivity() {
//
//    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
//    private lateinit var googleSignInClient: GoogleAuthClient
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        // Inisialisasi GoogleSignInClient
//        googleSignInClient = GoogleAuthClient(this)
//
//        // Inisialisasi launcher untuk Google Sign-In
//        googleSignInLauncher = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                lifecycleScope.launch {
//                    try {
//                        val success = googleSignInClient.signin()
//                        if (success) {
//                            Toast.makeText(this@MainActivity, "Sign-In Successful", Toast.LENGTH_SHORT).show()
//                        } else {
//                            Toast.makeText(this@MainActivity, "Sign-In Failed", Toast.LENGTH_SHORT).show()
//                        }
//                    } catch (e: Exception) {
//                        Toast.makeText(this@MainActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } else {
//                Toast.makeText(this, "Sign-In Canceled", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Menyiapkan UI
//        setContent {
//            ProjectpamTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Column(
//                        modifier = Modifier
//                            .padding(innerPadding)
//                            .fillMaxSize(),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Center
//                    ) {
//                        Button(onClick = { launchGoogleSignIn() }) {
//                            Text("Sign In with Google")
//                        }
//                        Button(onClick = { signOut() }) {
//                            Text("Sign Out")
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun launchGoogleSignIn() {
//        val signInIntent = googleSignInClient.getSignInIntent()
//        googleSignInLauncher.launch(signInIntent)
//    }
//
//    private fun signOut() {
//        lifecycleScope.launch {
//            googleSignInClient.signout()
//            Toast.makeText(this@MainActivity, "Signed Out Successfully", Toast.LENGTH_SHORT).show()
//        }
//    }
//}
//
