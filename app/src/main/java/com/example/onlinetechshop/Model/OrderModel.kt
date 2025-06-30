package com.example.onlinetechshop.Model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class OrderModel(
    val orderId: String = "",
    val userId: String = "",
    val items: List<ItemsModel> = listOf(),
    val totalPrice: Double = 0.0,
    val shippingName: String = "",
    val shippingPhone: String = "",
    val shippingAddress: String = "",
    val status: String = "Chờ xử lý", // hoặc enum cũng được
    val timestamp: Long = System.currentTimeMillis()
)
