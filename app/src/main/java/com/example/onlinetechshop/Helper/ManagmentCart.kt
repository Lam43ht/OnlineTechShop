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

        val index = listFood.indexOfFirst {
            it.title == item.title && it.model == item.model
        }

        if (index != -1) {
            listFood[index].numberIncart += item.numberIncart
        } else {
            listFood.add(item)
        }

        tinyDB.putListObject(cartKey, listFood)
        Toast.makeText(context, "Đã Thêm Sản Phẩm Vào Giỏ Hàng", Toast.LENGTH_SHORT).show()
    }

    fun getListCart(): ArrayList<ItemsModel> {
        return tinyDB.getListObject(cartKey) ?: arrayListOf()
    }

    fun minusItem(listFood: SnapshotStateList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        if (listFood[position].numberIncart == 1) {
            listFood.removeAt(position)
        } else {
            listFood[position].numberIncart--
        }
        tinyDB.putListObject(cartKey, ArrayList(listFood))
        listener.onChanged()
    }

    fun plusItem(listFood: SnapshotStateList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        val item = listFood[position]
        val model = item.model.firstOrNull()
        val stock = item.stockPerModel[model] ?: 0

        if (item.numberIncart < stock) {
            listFood[position].numberIncart++
            tinyDB.putListObject(cartKey, ArrayList(listFood))
            listener.onChanged()
        } else {
            Toast.makeText(context, "Số lượng vượt quá tồn kho!", Toast.LENGTH_SHORT).show()
        }
    }

    fun getTotalFee(): Double {
        val listFood = getListCart()
        var fee = 0.0
        for (item in listFood) {
            fee += item.price * item.numberIncart
        }
        return fee
    }

    fun clearCart() {
        tinyDB.putListObject(cartKey, arrayListOf())
    }

    fun updateCartItem(index: Int, newItem: ItemsModel) {
        val list = getListCart()
        if (index in list.indices) {
            list[index] = newItem
            putListCart(list)
        }
    }

    // ✅ Thêm hàm hỗ trợ lưu lại cart (bạn chưa có)
    private fun putListCart(list: List<ItemsModel>) {
        tinyDB.putListObject(cartKey, ArrayList(list))
    }
    fun removeItem(listFood: SnapshotStateList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        if (position in listFood.indices) {
            listFood.removeAt(position) // Xoá khỏi danh sách hiển thị
            tinyDB.putListObject(cartKey, ArrayList(listFood)) // Cập nhật lại vào TinyDB
            listener.onChanged() // Gọi callback để cập nhật giao diện
        }
    }

}
