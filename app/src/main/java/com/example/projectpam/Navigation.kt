package com.example.projectpam

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.projectpam.model.AuthViewModel
import androidx.navigation.compose.composable
import com.example.projectpam.model.PostViewModel
import com.example.projectpam.model.ProfileViewModel
import com.example.projectpam.pages.authentication.LoginPage
import com.example.projectpam.pages.authentication.SignupPage
import com.example.projectpam.pages.MainPage
import com.example.projectpam.pages.PostPage
import com.example.projectpam.pages.friends.AddPage
import com.example.projectpam.pages.friends.FriendListPage
import com.example.projectpam.pages.friends.SearchPage
import com.example.projectpam.pages.home.HomePage
import com.example.projectpam.pages.profile.ProfilePage

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    postViewModel: PostViewModel
) {

    val navController = rememberNavController()
    var selectedScreen by remember { mutableStateOf("home") }

    NavHost(
        navController = navController,
        startDestination = "main",
        modifier = modifier
    ) {
        composable("main") {
            MainPage(navController = navController)
        }

        // Login Page
        composable("login") {
            LoginPage(
                modifier = modifier,
                navController = navController,
                authViewModel = authViewModel

            )
        }

        // Signup Page
        composable("signup") {
            SignupPage(
                modifier = modifier,
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // BottomNavBar Pages
        composable("home") {
            HomePage(
                authViewModel = authViewModel,
                navController = navController,
                postViewModel = postViewModel,
                selectedScreen = selectedScreen,
                onScreenSelected = { screen ->
                    selectedScreen = screen
                    navController.navigate(screen) {
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        }

        composable("friends") {
            FriendListPage(selectedScreen = selectedScreen,
                onScreenSelected = { screen ->
                    selectedScreen = screen
                    navController.navigate(screen) {
                        popUpTo("home") { inclusive = false }
                        }
                },
                navController = navController
        )
        }
        // Profile Page
        composable("profile") {
            ProfilePage(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable("search_page/{query}") { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchPage(navController, initialQuery = query)
        }
        composable("add") {
            AddPage(
                navController = navController
            )
        }
        composable("post") {
            PostPage(
                authViewModel = authViewModel,
                navController = navController,

                postViewModel = postViewModel,
                selectedScreen = selectedScreen,
                onScreenSelected = { screen ->
                    selectedScreen = screen
                    navController.navigate(screen) {
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        }
    }
}

