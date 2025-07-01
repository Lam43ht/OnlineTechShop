package com.example.onlinetechshop.Activity

import android.os.Bundle
import java.text.NumberFormat
import java.util.Locale
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.example.onlinetechshop.Model.OrderModel
import com.example.onlinetechshop.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.onlinetechshop.Helper.formatVND

class FollowActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        setContent {
            FollowOrderScreen(userId,onBackClick = {finish()})
        }
    }
}

@Composable
fun FollowOrderScreen(userId: String, onBackClick: () -> Unit) {
    val orderList = remember { mutableStateListOf<OrderModel>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val db = FirebaseDatabase.getInstance().getReference("Orders").child(userId)
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                for (orderSnap in snapshot.children) {
                    val order = orderSnap.getValue(OrderModel::class.java)
                    if (order != null) {
                        orderList.add(order)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Không thể tải đơn hàng", Toast.LENGTH_SHORT).show()
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ConstraintLayout(modifier = Modifier.padding(top = 36.dp)) {
            val (backBtn, cartTxt) = createRefs()
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(cartTxt) { centerTo(parent) },
                text = "Đơn Hàng Của Bạn",
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

        Spacer(modifier = Modifier.height(16.dp))

        if (orderList.isEmpty()) {
            Text(
                text = "Bạn chưa đặt đơn hàng nào.",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                color = Color.Gray
            )
        } else {
            LazyColumn {
                items(orderList) { order ->
                    ExpandableOrderCard(order = order, userId = userId)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}


@Composable
fun ExpandableOrderCard(order: OrderModel, userId: String) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Mã đơn: ${order.orderId}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(if (expanded) "▲" else "▼", color = Color.Gray)
            }

            Text("Tên người nhận: ${order.shippingName}")
            Text("SĐT: ${order.shippingPhone}")
            Text("Địa chỉ: ${order.shippingAddress}")
            Text("Tổng tiền: ${formatVND(order.totalPrice)}", color = Color(0xFF2E7D32))
            Text(
                "Trạng thái: ${order.status ?: "Chờ xử lý"}",
                color = Color.Blue,
                fontWeight = FontWeight.Medium
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Danh sách sản phẩm:", fontWeight = FontWeight.Bold)

                order.items.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = item.picUrl.firstOrNull(),
                            contentDescription = item.title,
                            modifier = Modifier.size(60.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Model: ${item.model.firstOrNull() ?: "Không rõ"}", fontSize = 12.sp)
                            Text("Giá: ${formatVND(item.price)}", fontSize = 12.sp, color = Color.Gray)
                            Text("Số lượng: ${item.numberIncart}", fontSize = 12.sp)
                            Text(
                                text = "Tổng: ${formatVND(item.price * item.numberIncart)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF388E3C)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (order.status != "Đã giao" && order.status != "Đã huỷ") {
                    Button(
                        onClick = { showConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Huỷ đơn hàng", color = Color.White)
                    }
                }
            }
        }

        // ✅ Hộp thoại xác nhận huỷ
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Xác nhận huỷ đơn") },
                text = { Text("Bạn có chắc chắn muốn huỷ đơn hàng này không?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showConfirmDialog = false
                            val rootRef = FirebaseDatabase.getInstance().reference

                            // 1. Hoàn lại tồn kho cho từng item
                            for (item in order.items) {
                                val modelName = item.model.firstOrNull() ?: continue
                                val itemId = item.id ?: continue
                                val productRef = rootRef.child("Items").child(itemId)

                                // ✅ Hoàn lại stockPerModel
                                productRef.child("stockPerModel").child(modelName).get()
                                    .addOnSuccessListener { snapshot ->
                                        val currentStock = snapshot.getValue(Int::class.java) ?: 0
                                        productRef.child("stockPerModel").child(modelName)
                                            .setValue(currentStock + item.numberIncart)
                                    }

                                // ✅ Hoàn lại quantity
                                productRef.child("quantity").get()
                                    .addOnSuccessListener { snapshot ->
                                        val currentQty = snapshot.getValue(Int::class.java) ?: 0
                                        productRef.child("quantity")
                                            .setValue(currentQty + item.numberIncart)
                                    }
                            }

                            // 2. Cập nhật trạng thái đơn hàng
                            rootRef.child("Orders")
                                .child(userId)
                                .child(order.orderId)
                                .child("status")
                                .setValue("Đã huỷ")
                                .addOnSuccessListener {
                                    Toast.makeText(context, "✅ Đã huỷ đơn hàng và hoàn kho", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "❌ Lỗi khi huỷ đơn", Toast.LENGTH_SHORT).show()
                                }
                        }
                    ) {
                        Text("Huỷ đơn", color = Color.Red)
                    }
                }
                ,
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Đóng")
                    }
                }
            )
        }
    }
}

