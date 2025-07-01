package com.example.onlinetechshop.Model

import android.os.Parcel
import android.os.Parcelable

data class ItemsModel(
    var title: String = "",
    var description: String = "",
    var picUrl: ArrayList<String> = ArrayList(),
    var model: ArrayList<String> = ArrayList(),
    var price: Double = 0.0,
    var rating: Double = 0.0,
    var numberIncart: Int = 0,
    var showRecommended: Boolean = false,
    var categoryId: Long = 0L,
    var id: String? = null, // ✅ ID sản phẩm trong Firebase
    var quantity: Int = 0, // ✅ Tổng số tồn kho
    var stockPerModel: HashMap<String, Int> = HashMap() // ✅ Số lượng theo từng model
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.createStringArrayList() as ArrayList<String>,
        parcel.createStringArrayList() as ArrayList<String>,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),
        parcel.readString(), // id
        parcel.readInt(),    // quantity
        hashMapOf<String, Int>().apply {
            val size = parcel.readInt()
            for (i in 0 until size) {
                val key = parcel.readString() ?: ""
                val value = parcel.readInt()
                this[key] = value
            }
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeStringList(picUrl)
        parcel.writeStringList(model)
        parcel.writeDouble(price)
        parcel.writeDouble(rating)
        parcel.writeInt(numberIncart)
        parcel.writeByte(if (showRecommended) 1 else 0)
        parcel.writeLong(categoryId)
        parcel.writeString(id)
        parcel.writeInt(quantity)
        parcel.writeInt(stockPerModel.size)
        for ((key, value) in stockPerModel) {
            parcel.writeString(key)
            parcel.writeInt(value)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ItemsModel> {
        override fun createFromParcel(parcel: Parcel): ItemsModel = ItemsModel(parcel)
        override fun newArray(size: Int): Array<ItemsModel?> = arrayOfNulls(size)
    }
}
