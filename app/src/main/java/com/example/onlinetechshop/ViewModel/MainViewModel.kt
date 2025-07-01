package com.example.onlinetechshop.ViewModel

import android.app.DownloadManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.onlinetechshop.Model.CategoryModel
import com.example.onlinetechshop.Model.ItemsModel
import com.example.onlinetechshop.Model.SliderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.sync.Mutex
import perfetto.protos.AndroidMemoryUnaggregatedMetric

class MainViewModel:ViewModel() {

    private val firebaseDatabase: FirebaseDatabase
        get() = FirebaseDatabase.getInstance()
    private val _category= MutableLiveData<MutableList<CategoryModel>>()
    private val _banner= MutableLiveData<List<SliderModel>>()
    private val _recommended= MutableLiveData<MutableList<ItemsModel>>()
    val allItems = MutableLiveData<List<ItemsModel>>()

    val banners:LiveData<List<SliderModel>> = _banner
    val categories:LiveData<MutableList<CategoryModel>> = _category
    val recommended: LiveData<MutableList<ItemsModel>> = _recommended

    fun observeRecommendedRealtime() {
        val ref = FirebaseDatabase.getInstance().getReference("Items")
        ref.orderByChild("showRecommended").equalTo(true)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = mutableListOf<ItemsModel>()
                    for (itemSnap in snapshot.children) {
                        val item = itemSnap.getValue(ItemsModel::class.java)
                        if (item != null) result.add(item)
                    }
                    _recommended.value = result
                }

                override fun onCancelled(error: DatabaseError) {
                    // handle error
                }
            })
    }

    fun loadAllItems() {
        val ref = FirebaseDatabase.getInstance().getReference("Items")
        ref.get().addOnSuccessListener { snapshot ->
            val list = snapshot.children.mapNotNull { it.getValue(ItemsModel::class.java) }
            allItems.value = list
        }
    }

    fun loadFiltered(id: Long){
        val Ref=firebaseDatabase.getReference("Items")
        val query: Query = Ref.orderByChild("categoryId").equalTo(id.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()
                for(childSnapshot in snapshot.children){
                    val list = childSnapshot.getValue(ItemsModel::class.java)
                    if(list != null){
                        lists.add(list)
                    }
                }
                _recommended.value = lists
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    //Từng sản phẩm
    fun loadRecommended(){
        val Ref=firebaseDatabase.getReference("Items")
        val query: Query = Ref.orderByChild("showRecommended").equalTo(true)
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()
                for(childSnapshot in snapshot.children){
                    val list = childSnapshot.getValue(ItemsModel::class.java)
                    if(list != null){
                        lists.add(list)
                    }
                }
                _recommended.value = lists
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun loadBanner(){
        val Ref=firebaseDatabase.getReference("Banner")
        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<SliderModel>()
                for (childSnapshot in snapshot.children){
                    val list = childSnapshot.getValue(SliderModel::class.java)
                    if(list != null){
                        lists.add(list)
                    }
                }
                _banner.value = lists
            }

            override fun onCancelled(error: DatabaseError) {

            }

            
        })
    }
    //Loại sản phẩm
    fun loadCategory(){
        val Ref = firebaseDatabase.getReference("Category")
        Ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists= mutableListOf<CategoryModel>()
                for(childSnapshot in snapshot.children){
                    val list = childSnapshot.getValue(CategoryModel::class.java)
                    if(list != null){
                        lists.add(list)
                    }
                }
                _category.value = lists
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}