package com.mindshift.anxiety.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mindshift.anxiety.ui.screens.AnxietyScreen
import com.mindshift.anxiety.ui.screens.AuthScreen
import com.mindshift.anxiety.viewmodel.AuthViewModel

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Anxiety : Screen("anxiety")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    val startDestination = when (isLoggedIn) {
        true -> Screen.Anxiety.route
        false -> Screen.Auth.route
        null -> Screen.Auth.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Anxiety.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Anxiety.route) {
            AnxietyScreen(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Anxiety.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
