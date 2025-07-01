package com.example.onlinetechshop.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.TextButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.onlinetechshop.R
import com.example.onlinetechshop.ViewModel.AuthState
import com.example.onlinetechshop.ViewModel.AuthViewModel
import com.example.onlinetechshop.pages.AuthActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val authViewModel by viewModels<AuthViewModel>()
        super.onCreate(savedInstanceState)
        setContent {
            ProfileScreen(
                authViewModel = authViewModel,
                onSignOut = {
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish()
                },
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onSignOut: () -> Unit,
    onBackClick: () -> Unit
) {
    val authState = authViewModel.authState.observeAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    // Nếu đăng xuất, quay lại login
    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            onSignOut()
        }
    }

    // UI chính
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues()) // để không bị lấn status bar
            .padding(16.dp)
    ) {
        // Header với nút back
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (backBtn, titleTxt) = createRefs()

            Text(
                text = "Thông Tin Tài Khoản",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .constrainAs(titleTxt) { centerTo(parent) }
                    .padding(bottom = 5.dp)
            )

            Image(painter = painterResource(R.drawable.back),
                contentDescription = null,
                modifier = Modifier
                    .clickable{
                        onBackClick()
                    }
                    .constrainAs(backBtn){
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📧 Email", fontWeight = FontWeight.Medium, color = Color.Gray)
                Text(currentUser?.email ?: "Không có", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(12.dp))

                Text("🆔 UID", fontWeight = FontWeight.Medium, color = Color.Gray)
                Text(currentUser?.uid ?: "Không có", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { showChangePasswordDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
        ) {
            Text("🔒 Đổi mật khẩu", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { authViewModel.signout() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.green))
        ) {
            Text("🚪 Đăng xuất", color = Color.White, fontSize = 16.sp)
        }

        if (showChangePasswordDialog) {
            AlertDialog(
                onDismissRequest = { showChangePasswordDialog = false },
                title = {
                    Text("🔐 Đổi mật khẩu", fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = { Text("Mật khẩu hiện tại") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("Mật khẩu mới") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val email = currentUser?.email ?: return@TextButton
                        val credential = EmailAuthProvider.getCredential(email, currentPassword)

                        currentUser.reauthenticate(credential)
                            .addOnSuccessListener {
                                if (newPassword.length < 6) {
                                    Toast.makeText(context, "Mật khẩu mới phải ≥ 6 ký tự", Toast.LENGTH_SHORT).show()
                                } else {
                                    currentUser.updatePassword(newPassword)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                                            showChangePasswordDialog = false
                                            currentPassword = ""
                                            newPassword = ""
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Lỗi khi đổi mật khẩu", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show()
                            }
                    }) {
                        Text("Xác nhận", color = MaterialTheme.colorScheme.primary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showChangePasswordDialog = false }) {
                        Text("Huỷ", color = MaterialTheme.colorScheme.secondary)
                    }
                }
            )
        }
    }
}
