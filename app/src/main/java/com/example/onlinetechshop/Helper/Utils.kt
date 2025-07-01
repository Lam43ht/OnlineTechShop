package com.example.onlinetechshop.Helper

import java.text.NumberFormat
import java.util.Locale

fun formatVND(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return formatter.format(amount)
}
