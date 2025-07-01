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
                text = item.price.toVND(),
                fontSize = 22.sp,
                color = colorResource(R.color.green),
                fontWeight = FontWeight.Bold
            )
        }

        RatingBar(rating = item.rating)
        Text(
            text = "📦 Tổng tồn kho: ${item.quantity} sản phẩm",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        ModelSelector(
            models = item.model,
            selectedModeIndex = selectedModelIndex,
            onModelSelected = onModelSelected,
            stockPerModel = item.stockPerModel
        )
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            // Nếu có model được chọn, hiển thị riêng tồn của model đó
            if (selectedModelIndex in item.model.indices) {
                val selected = item.model[selectedModelIndex]
                val selectedStock = item.stockPerModel[selected] ?: 0
                Text(
                    text = "🟢 Còn lại: $selectedStock sản phẩm của model '$selected'",
                    fontSize = 14.sp,
                    color = Color(0xFF388E3C),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

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
                        val selectedModelStock = item.stockPerModel[selectedModel] ?: 0
                        if (selectedModelStock <= 0) {
                            Toast.makeText(context, "Model này đã hết hàng!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

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
    onModelSelected: (Int) -> Unit,
    stockPerModel: Map<String, Int>
){LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
    itemsIndexed(models) { index, model ->
        val isSelected = index == selectedModeIndex
        val stock = stockPerModel[model] ?: 0
        val isOutOfStock = stock <= 0

        val borderColor = if (isSelected) colorResource(R.color.green) else Color.Transparent
        val bgColor = when {
            isSelected -> colorResource(R.color.lightGreen)
            isOutOfStock -> Color.LightGray
            else -> colorResource(R.color.lightGrey)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(end = 8.dp)
                .height(IntrinsicSize.Min)
                .background(bgColor, shape = RoundedCornerShape(10.dp))
                .then(
                    if (isSelected) Modifier.border(1.dp, borderColor, RoundedCornerShape(10.dp))
                    else Modifier
                )
                .then(
                    if (!isOutOfStock) Modifier.clickable { onModelSelected(index) }
                    else Modifier
                )
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = model,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = if (isOutOfStock) Color.Gray
                else if (isSelected) colorResource(R.color.green)
                else colorResource(R.color.black)
            )
            if (isOutOfStock) {
                Text(
                    text = "Hết hàng",
                    fontSize = 10.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}}

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
// Thêm cuối file tiện ích định dạng tiền
fun Double.toVND(): String {
    return String.format("%,.0f ₫", this).replace(",", ".")
}
