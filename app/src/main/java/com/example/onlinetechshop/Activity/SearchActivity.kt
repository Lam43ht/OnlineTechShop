//package com.example.onlinetechshop.Activity
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowDropDown
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.constraintlayout.compose.ConstraintLayout
//import coil.compose.AsyncImage
//import com.example.onlinetechshop.Model.ItemsModel
//import com.example.onlinetechshop.R
//import com.example.onlinetechshop.ViewModel.MainViewModel
//import com.example.onlinetechshop.Helper.formatVND
//
//class SearchActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            SearchScreen(onBackClick = {finish()})
//        }
//    }
//}
//
//@Composable
//fun SearchScreen(viewModel: MainViewModel = MainViewModel(), onBackClick: () -> Unit) {
//    val allItems = remember { mutableStateListOf<ItemsModel>() }
//    var query by remember { mutableStateOf("") }
//    var selectedCategoryId by remember { mutableStateOf(-1) }
//    var selectedModel by remember { mutableStateOf("Tất cả") }
//    var minPrice by remember { mutableStateOf("") }
//    var maxPrice by remember { mutableStateOf("") }
//    var minRating by remember { mutableStateOf("") }
//
//    val context = LocalContext.current
//
//    // Load dữ liệu
//    LaunchedEffect(Unit) {
//        viewModel.loadAllItems()
//        viewModel.allItems.observeForever {
//            allItems.clear()
//            allItems.addAll(it)
//        }
//    }
//
//    val allCategories = listOf(
//        "Tất cả" to -1,
//        "Pc" to 0,
//        "Smartphone" to 1,
//        "Headphone" to 2,
//        "Console" to 3,
//        "Camera" to 4,
//        "Smartwatch" to 5
//    )
//
//    // Model theo category
//    val allModels = remember(allItems, selectedCategoryId) {
//        val filtered = if (selectedCategoryId == -1) {
//            allItems
//        } else {
//            allItems.filter { it.categoryId == selectedCategoryId.toLong() }
//        }
//        val models = filtered.flatMap { it.model }.toSet().toMutableList()
//        models.sort()
//        listOf("Tất cả") + models
//    }
//
//    val filteredItems = allItems.filter { item ->
//        val matchName = item.title.contains(query, ignoreCase = true)
//        val matchCategory = selectedCategoryId == -1 || item.categoryId == selectedCategoryId.toLong()
//        val matchModel = selectedModel == "Tất cả" || item.model.contains(selectedModel)
//        val matchMinPrice = minPrice.toDoubleOrNull()?.let { item.price >= it } ?: true
//        val matchMaxPrice = maxPrice.toDoubleOrNull()?.let { item.price <= it } ?: true
//        val matchMinRating = minRating.toDoubleOrNull()?.let { item.rating >= it } ?: true
//
//        matchName && matchCategory && matchModel && matchMinPrice && matchMaxPrice && matchMinRating
//    }
//
//    Column(modifier = Modifier
//        .fillMaxSize()
//        .padding(16.dp)) {
//        ConstraintLayout(modifier = Modifier.padding(top = 8.dp)) {
//            val (backBtn,cartTxt) = createRefs()
//            Text(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .constrainAs (cartTxt){ centerTo(parent) },
//                text = "Tìm Kiếm Nâng Cao",
//                textAlign = TextAlign.Center,
//                fontWeight = FontWeight.Bold,
//                fontSize = 25.sp
//            )
//
//            Image(painter = painterResource(R.drawable.back),
//                contentDescription = null,
//                modifier = Modifier
//                    .clickable{
//                        onBackClick()
//                    }
//                    .constrainAs(backBtn){
//                        top.linkTo(parent.top)
//                        bottom.linkTo(parent.bottom)
//                        start.linkTo(parent.start)
//                    }
//            )
//        }
//
//        Spacer(Modifier.height(8.dp))
//        OutlinedTextField(
//            value = query,
//            onValueChange = { query = it },
//            label = { Text("Tìm theo tên sản phẩm") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(Modifier.height(8.dp))
//        DropdownSelector(
//            label = "Chọn loại sản phẩm",
//            options = allCategories.map { it.first },
//            selectedOption = allCategories.find { it.second == selectedCategoryId }?.first ?: "Tất cả",
//            onOptionSelected = { option ->
//                selectedCategoryId = allCategories.find { it.first == option }?.second ?: -1
//                selectedModel = "Tất cả"
//            }
//        )
//
//        Spacer(Modifier.height(8.dp))
//        DropdownSelector(
//            label = "Chọn dòng sản phẩm (Model)",
//            options = allModels,
//            selectedOption = selectedModel,
//            onOptionSelected = { selectedModel = it }
//        )
//
//        Spacer(Modifier.height(8.dp))
//        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//            OutlinedTextField(
//                value = minPrice,
//                onValueChange = { minPrice = it },
//                label = { Text("Giá từ") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                modifier = Modifier.weight(1f)
//            )
//            OutlinedTextField(
//                value = maxPrice,
//                onValueChange = { maxPrice = it },
//                label = { Text("Đến") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                modifier = Modifier.weight(1f)
//            )
//        }
//
//        Spacer(Modifier.height(8.dp))
//        OutlinedTextField(
//            value = minRating,
//            onValueChange = { minRating = it },
//            label = { Text("⭐ Đánh giá từ bao nhiêu trở lên") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(Modifier.height(12.dp))
//        Divider()
//        Spacer(Modifier.height(8.dp))
//
//        LazyColumn {
//            items(filteredItems.size) { index ->
//                val item = filteredItems[index]
//                ProductSearchItem(item = item) {
//                    val intent = Intent(context, DetailActivity::class.java)
//                    intent.putExtra("object", item)
//                    context.startActivity(intent)
//                }
//            }
//            if (filteredItems.isEmpty()) {
//                item {
//                    Text("❌ Không tìm thấy sản phẩm phù hợp", color = MaterialTheme.colorScheme.error)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun DropdownSelector(
//    label: String,
//    options: List<String>,
//    selectedOption: String,
//    onOptionSelected: (String) -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//
//    Box(modifier = Modifier.fillMaxWidth()) {
//        OutlinedTextField(
//            value = selectedOption,
//            onValueChange = {},
//            label = { Text(label) },
//            modifier = Modifier.fillMaxWidth(),
//            readOnly = true,
//            trailingIcon = {
//                IconButton(onClick = { expanded = true }) {
//                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
//                }
//            }
//        )
//        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
//            options.forEach { option ->
//                DropdownMenuItem(
//                    text = { Text(option) },
//                    onClick = {
//                        onOptionSelected(option)
//                        expanded = false
//                    }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun ProductSearchItem(item: ItemsModel, onClick: () -> Unit) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onClick() }
//            .padding(vertical = 8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        AsyncImage(
//            model = item.picUrl.firstOrNull(),
//            contentDescription = item.title,
//            modifier = Modifier.size(64.dp),
//        )
//        Spacer(modifier = Modifier.width(12.dp))
//        Column {
//            Text(item.title, style = MaterialTheme.typography.bodyLarge)
//            Text("💵 ${formatVND(item.price)}", style = MaterialTheme.typography.bodySmall)
//            Text("⭐ ${item.rating} điểm", style = MaterialTheme.typography.bodySmall)
//        }
//    }
//}
package com.example.onlinetechshop.Activity

import android.content.Intent
import androidx.compose.material3.TextFieldDefaults
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.example.onlinetechshop.Model.ItemsModel
import com.example.onlinetechshop.R
import com.example.onlinetechshop.ViewModel.MainViewModel
import com.example.onlinetechshop.Helper.formatVND

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchScreen(onBackClick = { finish() })
        }
    }
}

@Composable
fun SearchScreen(viewModel: MainViewModel = MainViewModel(), onBackClick: () -> Unit) {
    val allItems = remember { mutableStateListOf<ItemsModel>() }
    var query by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf(-1) }
    var selectedModel by remember { mutableStateOf("Tất cả") }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var minRating by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadAllItems()
        viewModel.allItems.observeForever {
            allItems.clear()
            allItems.addAll(it)
        }
    }

    val allCategories = listOf(
        "Tất cả" to -1,
        "Pc" to 0,
        "Smartphone" to 1,
        "Headphone" to 2,
        "Console" to 3,
        "Camera" to 4,
        "Smartwatch" to 5
    )
    // ✅ Tên hiển thị đang được chọn
    val selectedCategoryName = allCategories.find { it.second == selectedCategoryId }?.first ?: "Tất cả"
    val allModels = remember(allItems, selectedCategoryId) {
        val filtered = if (selectedCategoryId == -1) allItems else allItems.filter { it.categoryId == selectedCategoryId.toLong() }
        val models = filtered.flatMap { it.model }.toSet().toMutableList()
        models.sort()
        listOf("Tất cả") + models
    }

    val filteredItems = allItems.filter { item ->
        val matchName = item.title.contains(query, ignoreCase = true)
        val matchCategory = selectedCategoryId == -1 || item.categoryId == selectedCategoryId.toLong()
        val matchModel = selectedModel == "Tất cả" || item.model.contains(selectedModel)
        val matchMinPrice = minPrice.toDoubleOrNull()?.let { item.price >= it } ?: true
        val matchMaxPrice = maxPrice.toDoubleOrNull()?.let { item.price <= it } ?: true
        val matchMinRating = minRating.toDoubleOrNull()?.let { item.rating >= it } ?: true

        matchName && matchCategory && matchModel && matchMinPrice && matchMaxPrice && matchMinRating
    }

    Column(modifier = Modifier.fillMaxSize().padding(5.dp)) {
        ConstraintLayout(modifier = Modifier.padding(top = 8.dp)) {
            val (backBtn, cartTxt) = createRefs()
            Text(
                modifier = Modifier.fillMaxWidth().constrainAs(cartTxt) { centerTo(parent) },
                text = "🔍 Tìm Kiếm Nâng Cao",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = null,
                modifier = Modifier.clickable { onBackClick() }.constrainAs(backBtn) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))) {
            Column(modifier = Modifier.padding(8.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Tìm theo tên sản phẩm") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                DropdownSelector(
                    label = "Chọn loại sản phẩm",
                    options = allCategories.map { it.first }, // chỉ truyền danh sách tên
                    selectedOption = selectedCategoryName,
                    onOptionSelected = { option ->
                        selectedCategoryId = allCategories.find { it.first == option }?.second ?: -1
                        selectedModel = "Tất cả" // reset model khi đổi category
                    }
                )
                Spacer(Modifier.height(12.dp))
                DropdownSelector(label = "Chọn dòng sản phẩm (Model)",
                    options = allModels,
                    selectedOption = selectedModel,
                    onOptionSelected = { selectedModel = it })
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = minPrice,
                        onValueChange = { minPrice = it },
                        label = { Text("Giá từ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = maxPrice,
                        onValueChange = { maxPrice = it },
                        label = { Text("Đến") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = minRating,
                    onValueChange = { minRating = it },
                    label = { Text("⭐ Đánh giá từ bao nhiêu trở lên") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Kết quả tìm kiếm:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredItems.size) { index ->
                val item = filteredItems[index]
                ProductSearchItem(item = item) {
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra("object", item)
                    context.startActivity(intent)
                }
            }
            if (filteredItems.isEmpty()) {
                item {
                    Text("❌ Không tìm thấy sản phẩm phù hợp", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun ProductSearchItem(item: ItemsModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.picUrl.firstOrNull(),
                contentDescription = item.title,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("💵 ${formatVND(item.price)}", fontSize = 14.sp, color = Color(0xFF2E7D32))
                Text("⭐ ${item.rating} điểm", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}
