package com.example.onlinetechshop.pages

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModel
import com.example.onlinetechshop.Activity.MainActivity
import com.example.onlinetechshop.MyAppNavigation
import com.example.onlinetechshop.ViewModel.AuthViewModel


class AuthActivity : ComponentActivity() {
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MyAppNavigation(
                    authViewModel = authViewModel,
                    onLoggedIn = {
                        startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                        finish()
                    } ,
                    onSignOut = { /* Có thể không cần dùng ở đây */ }
                )
            }
        }
    }
}

