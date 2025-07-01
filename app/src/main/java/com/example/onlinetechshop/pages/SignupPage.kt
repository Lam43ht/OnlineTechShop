//package com.example.onlinetechshop.pages
//
//import android.content.Intent
//import android.widget.Toast
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.material3.Button
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.example.onlinetechshop.Activity.MainActivity
//import com.example.onlinetechshop.ViewModel.AuthState
//import com.example.onlinetechshop.ViewModel.AuthViewModel
//import androidx.compose.runtime.getValue
//
//
//@Composable
//fun SignupPage(navController: NavController, authViewModel: AuthViewModel){
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//
//    val context = LocalContext.current
//    val authState = authViewModel.authState.observeAsState()
//    val currentState = authState.value
//
//    LaunchedEffect(authState.value) {
//        if (authState is AuthState.Error) {
//            Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    Column (
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ){
//        Text(
//            text = "Đăng Ký", fontSize = 32.sp
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = email,
//            onValueChange = {
//                email = it
//            },
//            label = {
//                Text(text = "Email")
//            }
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        OutlinedTextField(
//            value = password,
//            onValueChange = {
//                password = it
//            },
//            label = {
//                Text(text = "Mật Khẩu")
//            }
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(onClick = {
//                authViewModel.signup(email,password)
//        }) {
//            Text(text = "Đăng Ký")
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        TextButton(onClick = {
//            navController.navigate("login")
//        }) {
//            Text(text = "Bạn đã có tài khoản? Đăng Nhập!!")
//        }
//    }
//}
package com.example.onlinetechshop.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.onlinetechshop.ViewModel.AuthState
import com.example.onlinetechshop.ViewModel.AuthViewModel

@Composable
fun SignupPage(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val authState by authViewModel.authState.observeAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ✅ Ảnh nền mờ
        AsyncImage(
            model = "https://res.cloudinary.com/dplrrj7gj/image/upload/v1751343931/Hinh-nen-mau-xanh-la-cay-cute__4_hraz13.jpg", // 👉 Thay URL tùy ý
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.7f
        )

        // ✅ Giao diện đăng ký
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Đăng Ký", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(com.example.onlinetechshop.R.color.green),
                    unfocusedBorderColor = colorResource(com.example.onlinetechshop.R.color.green),
                    focusedLabelColor = colorResource(com.example.onlinetechshop.R.color.green)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(com.example.onlinetechshop.R.color.green),
                    unfocusedBorderColor = colorResource(com.example.onlinetechshop.R.color.green),
                    focusedLabelColor = colorResource(com.example.onlinetechshop.R.color.green)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { authViewModel.signup(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(com.example.onlinetechshop.R.color.green))
            ) {
                Text("Đăng Ký", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text("Đã có tài khoản? Đăng nhập ngay!", color = Color.DarkGray,fontSize = 15.sp,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}
