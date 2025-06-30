package com.example.onlinetechshop.Helper

import android.content.Context
import com.example.onlinetechshop.Model.OrderModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ManagmentOrder(private val context: Context, private val userId: String) {

    private val tinyDB = TinyDB(context)
    private val key = "OrderList_$userId"
    private val gson = Gson()

    // ✅ Lấy danh sách đơn hàng
    fun getOrders(): ArrayList<OrderModel> {
        val json = tinyDB.getString(key)
        if (json.isNullOrEmpty()) return arrayListOf()
        val type = object : TypeToken<ArrayList<OrderModel>>() {}.type
        return gson.fromJson(json, type)
    }

    // ✅ Thêm 1 đơn hàng mới
    fun addOrder(order: OrderModel) {
        val orders = getOrders()
        orders.add(order)
        saveOrders(orders)
    }

    // ✅ Sinh ID đơn hàng tiếp theo
    fun getNextOrderId(): Int {
        return getOrders().size + 1
    }

    // ✅ Ghi lại toàn bộ danh sách đơn hàng
    fun saveOrders(orders: ArrayList<OrderModel>) {
        val json = gson.toJson(orders)
        tinyDB.putString(key, json)
    }

    // ✅ Xoá toàn bộ đơn hàng của user (tuỳ chọn)
    fun clearOrders() {
        tinyDB.remove(key)
    }
}
