package com.example.onlinetechshop.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onlinetechshop.R
import androidx.activity.enableEdgeToEdge
import com.example.onlinetechshop.pages.AuthActivity

class IntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*window.clearFlags(FLAG_TRANSLUCENT_STATUS)
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR*/
        // Bỏ các API lỗi thời và sử dụng cách tiếp cận hiện đại
        enableEdgeToEdge()
        setContent {
            MaterialTheme { // Nên dùng theme custom thay vì MaterialTheme
                IntroScreen(
                    onClick = {
                        // Sửa thành startActivity rõ ràng
                        startActivity(Intent(this@IntroActivity, AuthActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun IntroScreen( onClick: () -> Unit={}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.intro_pic),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 24.dp)
                .size(width = 400.dp, height = 400.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.intro_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.intro_sub_title),
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Button(
            onClick = onClick,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.green))
        ) {
            Text(
                text = stringResource(id = R.string.letgo),
                color = Color.White,
                fontSize = 20.sp
            )
        }

        Text(
            text = stringResource(id = R.string.sign),
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
    }
}
