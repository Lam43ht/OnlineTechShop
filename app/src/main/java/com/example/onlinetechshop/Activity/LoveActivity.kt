package com.example.onlinetechshop.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.onlinetechshop.Helper.ManagmentFavorite
import com.example.onlinetechshop.Model.ItemsModel
import com.example.onlinetechshop.R
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout

class LoveActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
        val managmentFavorite = ManagmentFavorite(this, userId)

        setContent {
            LoveScreen(managmentFavorite,onBackClick = {finish()})
        }
    }
}

@Composable
fun LoveScreen(managmentFavorite: ManagmentFavorite, onBackClick: () -> Unit) {
    val favoriteItems = remember { mutableStateListOf<ItemsModel>() }

    LaunchedEffect(Unit) {
        favoriteItems.clear()
        favoriteItems.addAll(managmentFavorite.getFavoriteList())
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ConstraintLayout(modifier = Modifier.padding(top = 36.dp)) {
            val (backBtn,cartTxt) = createRefs()
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs (cartTxt){ centerTo(parent) },
                text = "❤\uFE0F Danh sách yêu thích",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
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

        if (favoriteItems.isEmpty()) {
            Text(text = "Bạn chưa thêm sản phẩm nào vào yêu thích.", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn {
                items(favoriteItems) { item ->
                    FavoriteItemCard(
                        item = item,
                        onRemove = {
                            managmentFavorite.removeFavorite(item)
                            favoriteItems.remove(item)
                        },
                        onClick = {
                            val intent = Intent(context, DetailActivity::class.java)
                            intent.putExtra("object", item)
                            context.startActivity(intent)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun FavoriteItemCard(item: ItemsModel, onRemove: () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.picUrl.firstOrNull(),
            contentDescription = item.title,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.title, fontWeight = FontWeight.Bold)
            Text(text = "💵 ${item.price} đ", color = Color.Gray, fontSize = 13.sp)
        }

        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Xoá khỏi yêu thích",
                tint = Color.Red
            )
        }
    }
}
