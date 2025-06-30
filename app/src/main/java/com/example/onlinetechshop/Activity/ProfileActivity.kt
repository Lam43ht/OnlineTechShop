package com.example.onlinetechshop.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import com.example.onlinetechshop.R
import com.example.onlinetechshop.ViewModel.AuthState
import com.example.onlinetechshop.ViewModel.AuthViewModel
import com.example.onlinetechshop.pages.AuthActivity
import java.nio.file.WatchEvent

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val authViewModel by viewModels<AuthViewModel>()
        super.onCreate(savedInstanceState)
        setContent {
            ProfileScreen(
                authViewModel = authViewModel,
                onSignOut = {
                    // Quay lại màn hình đăng nhập sau khi đăng xuất
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish() // Đóng ProfileActivity
                },
                onBackClick = {finish()}
            )
        }
    }
}
@Composable
fun ProfileScreen(authViewModel: AuthViewModel,
                  onSignOut: () -> Unit,
                  onBackClick: () -> Unit){
    val authState = authViewModel.authState.observeAsState()
    // Khi người dùng bị "Unauthenticated", gọi callback để quay lại màn login
    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            onSignOut()
        }
    }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ConstraintLayout(modifier = Modifier.padding(top = 8.dp)) {
            val (backBtn, cartTxt) = createRefs()
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(cartTxt) { centerTo(parent) },
                text = "Thông Tin Tài Khoản",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )

            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        onBackClick()
                    }
                    .constrainAs(backBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )
        }
    }
    Column (modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(text = "Thông Tin Tài Khoản", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = {authViewModel.signout()}) {
            Text(text = "Đăng xuất")
        }
    }

}
