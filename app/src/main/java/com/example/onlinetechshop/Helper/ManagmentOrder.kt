package com.example.onlinetechshop.Helper

import android.content.Context
import com.example.onlinetechshop.Model.OrderModel

class ManagmentOrder(private val context: Context, private val userId: String) {
    private val tinyDB = TinyDB(context)
    private val key = "OrderList_$userId"

    fun getOrders(): ArrayList<OrderModel> {
        return tinyDB.getListObject(key) ?: arrayListOf()
    }

    fun addOrder(order: OrderModel) {
        val orders = getOrders()
        orders.add(order)
        tinyDB.putListObject(key, orders)
    }

    fun getNextOrderId(): Int {
        return getOrders().size + 1
    }
}
