package com.example.onlinetechshop.Activity

import android.os.Bundle
import java.text.NumberFormat
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onlinetechshop.Helper.ManagmentCart
import com.example.onlinetechshop.R
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.example.onlinetechshop.Helper.ChangeNumberItemsListener
import com.example.onlinetechshop.Model.ItemsModel
import com.example.onlinetechshop.Model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.onlinetechshop.Helper.formatVND

class CartActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
        setContent {
            CartScreen(ManagmentCart(this,uid),
                onBackClick = {finish()})
        }
    }
}

@Composable
private fun CartScreen(
    managmentCart: ManagmentCart,
    onBackClick: () -> Unit
){
    val cartItems = remember { mutableStateListOf<ItemsModel>().apply { addAll(managmentCart.getListCart()) } }
    val tax = remember { mutableStateOf(0.0) }
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(cartItems) {
        calculatorCart(managmentCart, tax)
    }
    LaunchedEffect(Unit) {
        syncCartWithFirebase(cartItems, managmentCart) {
            cartItems.clear()
            cartItems.addAll(managmentCart.getListCart())
            calculatorCart(managmentCart, tax)
        }
    }


    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        ConstraintLayout(modifier = Modifier.padding(top = 36.dp)) {
            val (backBtn,cartTxt) = createRefs()
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs (cartTxt){ centerTo(parent) },
                text = "Giỏ Hàng Của Bạn",
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
        Spacer(modifier = Modifier.height(16.dp))
        if(cartItems.isEmpty()){
            Text(
                text = "Bạn chưa thêm sản phẩm nào vào giỏ hàng.",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                color = Color.Gray
            )
        }else{
            CartList(
                cartItems = cartItems, managmentCart
            ){
                cartItems.clear()
                cartItems.addAll(managmentCart.getListCart())
                calculatorCart(managmentCart, tax)
            }

            CartSummary(
                itemTotal = managmentCart.getTotalFee(),
                tax = tax.value,
                delivery = 10000.0,
                cartItems = cartItems,
                managmentCart = managmentCart,
                onPlaceOrderClick = { showDialog.value = true }
            )
        }
        if (showDialog.value) {
            ShippingInfoDialog(
                onDismiss = { showDialog.value = false },
                onConfirm = { name, phone, address ->
                    placeOrder(name, phone, address, cartItems, managmentCart, context)
                    showDialog.value = false
                }
            )
        }
    }
}

@Composable
fun CartSummary(itemTotal: Double,
                tax: Double,
                delivery: Double,
                cartItems: SnapshotStateList<ItemsModel>,
                managmentCart: ManagmentCart,
                onPlaceOrderClick: () -> Unit
) {
    val total = itemTotal + tax + delivery
    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp)
    ){
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
        ){
            Text(
                text = "Tổng Sản Phẩm: ",
                Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.grey)
            )
            Text(text = formatVND(itemTotal))
        }

        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
        ){
            Text(
                text = "Thuế: ",
                Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.grey)
            )
            Text(text = formatVND(tax))
        }

        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp)
        ){
            Text(
                text = "Phí Vận Chuyển : ",
                Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.grey)
            )
            Text(text = formatVND(delivery))
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colorResource(R.color.grey))
                .padding(vertical = 8.dp)
        )

        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
        ){
            Text(
                text = "Tổng Tiền : ",
                Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.grey)
            )
            Text(text = formatVND(total))
        }
        // ✅ Nút Đặt Hàng mở dialog
        Button(
            onClick = onPlaceOrderClick,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.green)),
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Đặt Hàng",
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}
@Composable
fun ShippingInfoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank() && phone.isNotBlank() && address.isNotBlank()) {
                    onConfirm(name, phone, address)
                }
            }) {
                Text("Xác nhận")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Huỷ") }
        },
        title = { Text("Thông Tin Giao Hàng") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Họ tên") })
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Số điện thoại") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Địa chỉ") })
            }
        }
    )
}

fun calculatorCart(managmentCart: ManagmentCart, tax: MutableState<Double>){
    val percentTax = 0.02
    tax.value = Math.round((managmentCart.getTotalFee()*percentTax)*100)/100.0
}

fun placeOrder(
    name: String,
    phone: String,
    address: String,
    cartItems: List<ItemsModel>,
    managmentCart: ManagmentCart,
    context: android.content.Context
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val total = cartItems.sumOf { it.price * it.numberIncart }
    val orderId = "ORDER_${System.currentTimeMillis()}"
    // ✅ Định nghĩa ngày hiện tại
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val orderDate = sdf.format(Date())

    val order = OrderModel(
        orderId = orderId,
        userId = userId,
        items = cartItems,
        totalPrice = total,
        shippingName = name,
        shippingPhone = phone,
        shippingAddress = address,
        status = "Chờ xử lý",
        orderDate = orderDate
    )

    val db = FirebaseDatabase.getInstance().reference

    // ✅ Ghi đơn hàng vào Firebase
    db.child("Orders").child(userId).child(orderId).setValue(order)
        .addOnSuccessListener {
            // ✅ Cập nhật tồn kho sau khi đặt hàng
            for (item in cartItems) {
                val modelName = item.model.firstOrNull() ?: continue
                val itemId = item.id ?: continue  // ⚠️ Đảm bảo ID tồn tại
                val productRef = db.child("Items").child(itemId)

                // ✅ Cập nhật tồn kho theo model
                productRef.child("stockPerModel").child(modelName).get()
                    .addOnSuccessListener { stockSnap ->
                        val currentStock = stockSnap.getValue(Int::class.java) ?: 0
                        val updatedStock = (currentStock - item.numberIncart).coerceAtLeast(0)
                        productRef.child("stockPerModel").child(modelName).setValue(updatedStock)
                    }

                // ✅ Cập nhật tổng quantity
                productRef.child("quantity").get().addOnSuccessListener { qtySnap ->
                    val currentQty = qtySnap.getValue(Int::class.java) ?: 0
                    val updatedQty = (currentQty - item.numberIncart).coerceAtLeast(0)
                    productRef.child("quantity").setValue(updatedQty)
                }
            }

            managmentCart.clearCart()
            Toast.makeText(context, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Lỗi đặt hàng!", Toast.LENGTH_SHORT).show()
        }
}



@Composable
fun CartList(
    cartItems: SnapshotStateList<ItemsModel>,
    managmentCart: ManagmentCart,
    onItemsChange: () -> Unit
)
{
    LazyColumn(Modifier.padding(top = 16.dp)){
        items (cartItems){item ->
            CartItem(cartItems,
                item = item,
                managmentCart = managmentCart,
                onItemsChange = onItemsChange)
        }
    }

}

@Composable
fun CartItem(
    cartItems: SnapshotStateList<ItemsModel>,
    item: ItemsModel,
    managmentCart: ManagmentCart,
    onItemsChange: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
    ){
        val (pic,modelTxt, titleTxt, feeEachTime, totalEachItem, Quantity) = createRefs()
        val selectedModel = item.model.firstOrNull()
        val stock = item.stockPerModel[selectedModel] ?: 0
        val context = LocalContext.current
        val (deleteBtn) = createRefs()
        Column(
            modifier = Modifier
                .width(90.dp)
                .constrainAs(pic) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(item.picUrl[0]),
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .background(
                        colorResource(R.color.lightGrey),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(8.dp)
            )
            Text(
                text = "Còn lại: $stock",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Text(
            text = "Loại: ${item.model.firstOrNull() ?: "Không rõ"}",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
                .constrainAs(modelTxt) {
                    top.linkTo(modelTxt.top, margin = (6).dp)
                    end.linkTo(parent.end)
                }
                .padding(end = 8.dp, top = 8.dp)
        )
        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(30.dp)
                .background(Color.Red, shape = RoundedCornerShape(6.dp))
                .clickable {
                    managmentCart.removeItem(
                        cartItems,
                        cartItems.indexOf(item),
                        object : ChangeNumberItemsListener {
                            override fun onChanged() {
                                onItemsChange()
                            }
                        }
                    )
                }
                .constrainAs(deleteBtn) {
                    top.linkTo(modelTxt.top, margin = (26).dp) // ✅ Đẩy nút lên so với model
                    end.linkTo(parent.end)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "X",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Text(
            text = item.title,
            modifier = Modifier
                .constrainAs (titleTxt){
                    start.linkTo(pic.end)
                    top.linkTo(parent.top)
                }
                .padding(start = 8.dp, top = 8.dp)
        )
        Text(text = formatVND(item.price), color = colorResource(R.color.green),
            modifier = Modifier
                .constrainAs (feeEachTime){
                    start.linkTo(titleTxt.start)
                    top.linkTo(titleTxt.bottom)
                }
                .padding(start = 8.dp, top = 8.dp)
            )

        Text(
            text = formatVND(item.numberIncart * item.price),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .constrainAs (totalEachItem){
                    start.linkTo(titleTxt.start)
                    bottom.linkTo(pic.bottom)
                }
                .padding(8.dp)
        )

        ConstraintLayout (modifier = Modifier
            .width(100.dp)
            .constrainAs (Quantity){
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
            .background(colorResource(R.color.lightGrey),
                shape = RoundedCornerShape(10.dp))
        ){
            val (plusCartBtn, minusCartBtn, numberItemTxt) = createRefs()
            Text(text = item.numberIncart.toString(),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs (numberItemTxt){
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                )

            //Tạo nút tăng sản phẩm
            Box(modifier = Modifier
                .padding(2.dp)
                .size(28.dp)
                .background(colorResource(R.color.green),
                    shape = RoundedCornerShape(10.dp))
                .constrainAs (plusCartBtn){
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .clickable{
                    if (item.numberIncart < stock) {
                        managmentCart.plusItem(
                            cartItems,
                            cartItems.indexOf(item),
                            object : ChangeNumberItemsListener {
                                override fun onChanged() {
                                    onItemsChange()
                                }
                            }
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Chỉ còn $stock sản phẩm cho lựa chọn này!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ){
                Text(
                    text = "+",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
            //Tạo nút giảm sản phẩm
            Box(modifier = Modifier
                .padding(2.dp)
                .size(28.dp)
                .background(colorResource(R.color.white),
                    shape = RoundedCornerShape(10.dp))
                .constrainAs (minusCartBtn){
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .clickable {
                    managmentCart.minusItem(
                        cartItems,
                        cartItems.indexOf(item),
                        object : ChangeNumberItemsListener {
                            override fun onChanged() {
                                onItemsChange()
                            }

                        })
                })
            {
                Text(
                    text = "-",
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,

                )
            }

        }
    }
}
fun syncCartWithFirebase(
    cartItems: SnapshotStateList<ItemsModel>,
    managmentCart: ManagmentCart,
    onItemsChange: (() -> Unit)? = null
) {
    val db = FirebaseDatabase.getInstance().reference

    cartItems.forEachIndexed { index, item ->
        val itemId = item.id ?: return@forEachIndexed
        val model = item.model.firstOrNull() ?: return@forEachIndexed

        db.child("Items").child(itemId).get().addOnSuccessListener { snapshot ->
            val productData = snapshot.value as? Map<*, *> ?: return@addOnSuccessListener

            val stockMap = productData["stockPerModel"] as? Map<*, *> ?: emptyMap<Any?, Any?>()
            val totalQuantity = (productData["quantity"] as? Long)?.toInt() ?: 0
            val updatedStock = (stockMap[model] as? Long)?.toInt() ?: 0

            val convertedStockMap = HashMap<String, Int>()
            for ((key, value) in stockMap) {
                val keyStr = key as? String ?: continue
                val valueInt = (value as? Long)?.toInt() ?: continue
                convertedStockMap[keyStr] = valueInt
            }

            // Cập nhật item mới
            val updatedItem = item.copy(
                stockPerModel = convertedStockMap,
                quantity = totalQuantity,
                numberIncart = minOf(item.numberIncart, updatedStock) // tránh vượt stock
            )

            // Cập nhật trong list cart (UI list)
            cartItems[index] = updatedItem

            // Cập nhật trong TinyDB
            managmentCart.updateCartItem(index, updatedItem)

            // Gọi callback cập nhật UI tổng tiền, nếu có
            onItemsChange?.invoke()
        }
    }
}


