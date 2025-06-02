package com.example.onlinetechshop.Helper

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.onlinetechshop.Model.ItemsModel


class ManagmentCart(private val context: Context, private val userId: String) {

    private val tinyDB = TinyDB(context)
    private val cartKey = "CartList_$userId" // ✅ key riêng cho từng người dùng

    fun insertItem(item: ItemsModel) {
        var listFood = getListCart()
        val existAlready = listFood.any { it.title == item.title }
        val index = listFood.indexOfFirst { it.title == item.title }

        if (existAlready) {
            listFood[index].numberIncart = item.numberIncart
        } else {
            listFood.add(item)
        }
        tinyDB.putListObject(cartKey, listFood) // ✅ dùng key riêng
        Toast.makeText(context, "Đã Thêm Sản Phẩm Vào Giỏ Hàng", Toast.LENGTH_SHORT).show()
    }

    fun getListCart(): ArrayList<ItemsModel> {
        //return tinyDB.getListObject("CartList") ?: arrayListOf()
        return tinyDB.getListObject(cartKey) ?: arrayListOf() // ✅ dùng key riêng
    }

    fun minusItem(listFood: SnapshotStateList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        if (listFood[position].numberIncart == 1) {
            listFood.removeAt(position)
        } else {
            listFood[position].numberIncart--
        }
        tinyDB.putListObject(cartKey, ArrayList(listFood)) // ✅ dùng key riêng
        listener.onChanged()
    }

    fun plusItem(listFood: SnapshotStateList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        listFood[position].numberIncart++
        tinyDB.putListObject(cartKey, ArrayList(listFood)) // ✅ dùng key riêng
        listener.onChanged()
    }

    fun getTotalFee(): Double {
        val listFood = getListCart()
        var fee = 0.0
        for (item in listFood) {
            fee += item.price * item.numberIncart
        }
        return fee
    }
}