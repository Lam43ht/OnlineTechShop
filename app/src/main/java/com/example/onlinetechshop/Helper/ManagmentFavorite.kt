package com.example.onlinetechshop.Helper

import android.content.Context
import android.widget.Toast
import com.example.onlinetechshop.Model.ItemsModel

class ManagmentFavorite(private val context: Context, private val userId: String) {

    private val tinyDB = TinyDB(context)
    private val favoriteKey = "FavoriteList_$userId"

    fun insertFavorite(item: ItemsModel) {
        val list = getFavoriteList().toMutableList()

        val alreadyExists = list.any { it.title == item.title }
        if (!alreadyExists) {
            list.add(item)
            tinyDB.putListObject(favoriteKey, ArrayList(list))
            Toast.makeText(context, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Sản phẩm đã có trong yêu thích", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeFavorite(item: ItemsModel) {
        val list = getFavoriteList().toMutableList()
        list.removeAll { it.title == item.title }
        tinyDB.putListObject(favoriteKey, ArrayList(list))
        Toast.makeText(context, "Đã xoá khỏi yêu thích", Toast.LENGTH_SHORT).show()
    }

    fun getFavoriteList(): List<ItemsModel> {
        return tinyDB.getListObject(favoriteKey) ?: listOf()
    }
}
