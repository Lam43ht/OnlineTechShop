package com.example.onlinetechshop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.onlinetechshop.ViewModel.AuthState
import com.example.onlinetechshop.ViewModel.AuthViewModel
import com.example.onlinetechshop.pages.LoginPage
import com.example.onlinetechshop.pages.SignupPage
import androidx.compose.runtime.getValue



@Composable
fun MyAppNavigation(authViewModel: AuthViewModel, onLoggedIn: () -> Unit, onSignOut: () -> Unit) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.observeAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> onLoggedIn()
            is AuthState.Unauthenticated -> onSignOut()
            else -> Unit
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginPage(navController = navController, authViewModel = authViewModel)
        }
        composable("signup") {
            SignupPage(navController = navController, authViewModel = authViewModel)
        }

    }
}
