package com.example.onlinetechshop.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import coil.compose.rememberAsyncImagePainter
import com.example.onlinetechshop.Helper.ManagmentCart
import com.example.onlinetechshop.Helper.ManagmentFavorite
import com.example.onlinetechshop.Model.ItemsModel
import com.example.onlinetechshop.R
import com.google.firebase.auth.FirebaseAuth

class DetailActivity : BaseActivity() {
    private lateinit var item: ItemsModel
    private lateinit var managmentCart: ManagmentCart
    private lateinit var managmentFavorite: ManagmentFavorite
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = intent.getParcelableExtra("object")!!
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "Người dùng không đăng nhập", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        managmentFavorite = ManagmentFavorite(this, userId)
        managmentCart = ManagmentCart(this, userId)

        setContent {
            var selectedModelIndex by remember { mutableStateOf(-1) }

            DetailScreen(
                item = item,
                selectedModelIndex = selectedModelIndex,
                onModelSelected = { selectedModelIndex = it },
                managmentCart = managmentCart,
                managmentFavorite = managmentFavorite,
                onBackClick = { finish() },
                onCartClick = {
                    startActivity(Intent(this, CartActivity::class.java))
                }
            )
        }
    }
}

@Composable
fun DetailScreen(
    item: ItemsModel,
    selectedModelIndex: Int,
    onModelSelected: (Int) -> Unit,
    managmentCart: ManagmentCart,
    managmentFavorite: ManagmentFavorite,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit
) {
    var selectedImageUrl by remember { mutableStateOf(item.picUrl.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(top = 36.dp, bottom = 16.dp)
                .fillMaxWidth()
        ) {
            val (back, fav) = createRefs()
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = "",
                modifier = Modifier
                    .clickable { onBackClick() }
                    .constrainAs(back) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )
            Image(
                painter = painterResource(R.drawable.fav_icon),
                contentDescription = "",
                modifier = Modifier
                    .clickable {
                        managmentFavorite.insertFavorite(item)
                    }
                    .constrainAs(fav) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
            )
        }

        Image(
            painter = rememberAsyncImagePainter(model = selectedImageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .background(colorResource(R.color.lightGrey), shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        )

        LazyRow(modifier = Modifier.padding(vertical = 16.dp)) {
            items(item.picUrl) { imageUrl ->
                ImageThumbnail(
                    imageUrl = imageUrl,
                    isSelected = selectedImageUrl == imageUrl,
                    onClick = { selectedImageUrl = imageUrl }
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = item.title,
                fontSize = 23.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(end = 16.dp)
            )
            Text(
                text = "$${item.price}",
                fontSize = 22.sp
            )
        }

        RatingBar(rating = item.rating)

        ModelSelector(
            models = item.model,
            selectedModeIndex = selectedModelIndex,
            onModelSelected = onModelSelected
        )

        Text(
            text = item.description,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            val context = LocalContext.current
            Button(
                onClick = {
                    if (selectedModelIndex == -1) {
                        Toast.makeText(context, "Vui lòng chọn dòng sản phẩm!", Toast.LENGTH_SHORT).show()
                    } else {
                        val selectedModel = item.model[selectedModelIndex]
                        val itemCopy = item.copy(
                            model = arrayListOf(selectedModel),
                            numberIncart = 1
                        )
                        managmentCart.insertItem(itemCopy)
                        Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.green)),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .height(50.dp)
            ) {
                Text(text = "Đặt Hàng", fontSize = 18.sp)
            }

            IconButton(
                onClick = onCartClick,
                modifier = Modifier.background(
                    colorResource(R.color.lightGrey),
                    shape = RoundedCornerShape(10.dp)
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.btn_2),
                    contentDescription = "Cart",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun RatingBar(rating: Double) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text(
            text = "Lựa chọn dòng sản phẩm",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Image(
            painter = painterResource(id = R.drawable.star),
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp)
        )

        Text(text = "$rating Đánh Giá", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ModelSelector(
    models: List<String>,
    selectedModeIndex: Int,
    onModelSelected: (Int) -> Unit
) {
    LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
        itemsIndexed(models) { index, model ->
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .height(48.dp)
                    .then(
                        if (index == selectedModeIndex) {
                            Modifier.border(1.dp, colorResource(R.color.green), RoundedCornerShape(10.dp))
                        } else {
                            Modifier
                        }
                    )
                    .background(
                        if (index == selectedModeIndex) colorResource(R.color.lightGreen)
                        else colorResource(R.color.lightGrey),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onModelSelected(index) }
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = model,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = if (index == selectedModeIndex) colorResource(R.color.green)
                    else colorResource(R.color.black),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun ImageThumbnail(
    imageUrl: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backColor = if (isSelected) colorResource(R.color.lightGreen) else colorResource(R.color.lightGrey)

    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(55.dp)
            .then(
                if (isSelected) {
                    Modifier.border(1.dp, colorResource(R.color.green), RoundedCornerShape(10.dp))
                } else {
                    Modifier
                }
            )
            .background(backColor, shape = RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = imageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        )
    }
}
