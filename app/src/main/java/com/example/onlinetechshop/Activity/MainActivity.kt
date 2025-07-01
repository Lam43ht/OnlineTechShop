package com.example.onlinetechshop.Activity

import androidx.compose.foundation.lazy.items
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.ModalDrawer
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.onlinetechshop.Model.CategoryModel
import com.example.onlinetechshop.Model.ItemsModel
import com.example.onlinetechshop.Model.SliderModel
import com.example.onlinetechshop.R
import com.example.onlinetechshop.ViewModel.AuthState
import com.example.onlinetechshop.ViewModel.AuthViewModel
import com.example.onlinetechshop.ViewModel.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.firebase.auth.FirebaseAuth
import okhttp3.internal.http2.Http2Reader
import androidx.compose.material3.IconButton
import com.example.onlinetechshop.Helper.formatVND

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val authViewModel : AuthViewModel by viewModels()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email ?: "Khách"
        setContent {
            val context = LocalContext.current
        MainActivityScreen(
            userName = userEmail,
            onCartClick = {
                startActivity(Intent(context, CartActivity::class.java))
            },
            onProfileClick = {
                startActivity(Intent(context, ProfileActivity::class.java)) // hoặc màn hình khác bạn muốn
            },
            onLookClick = {
                startActivity(Intent(context, SearchActivity::class.java))
            },
            onLoveClick = {
                startActivity(Intent(context, LoveActivity::class.java))
            },
            onFollowClick = {
                startActivity(Intent(context, FollowActivity::class.java))
            },
            )
        }
    }
}
@Composable
//@Preview
fun MainActivityScreen(userName: String ,onCartClick:() -> Unit = {}, onProfileClick: () -> Unit = {}, onLookClick: () -> Unit = {},onLoveClick: () -> Unit, onFollowClick: () -> Unit){
    //val viewModel = MainViewModel()
    val viewModel: MainViewModel = viewModel() // ✅ Đúng cách
    val banners = remember { mutableStateListOf<SliderModel>()}
    val categories = remember { mutableStateListOf<CategoryModel>()}
    val recommended = remember { mutableStateListOf<ItemsModel>()}
    var showBannerLoading  by remember { mutableStateOf(true)}
    var showCategoryLoading  by remember { mutableStateOf(true)}
    var showRecommendedLoading  by remember { mutableStateOf(true)}
    var showSearchDialog by remember { mutableStateOf(false) }
    val productList = remember { mutableStateListOf<ItemsModel>() }
    val context = LocalContext.current
    //Banner
    LaunchedEffect(Unit) {
        viewModel.loadBanner()
        viewModel.banners.observeForever{
            banners.clear()
            banners.addAll(it)
            showBannerLoading = false
        }
    }

    //Category
    LaunchedEffect(Unit) {
        viewModel.loadCategory()
        viewModel.categories.observeForever{
            categories.clear()
            categories.addAll(it)
            showCategoryLoading = false
        }
    }

    //Recommended
    LaunchedEffect(Unit) {
        //viewModel.loadRecommended()
        viewModel.observeRecommendedRealtime()
        viewModel.recommended.observeForever {
            recommended.clear()
            recommended.addAll(it)
            showRecommendedLoading = false
        }

    }

    ConstraintLayout(modifier = Modifier.background(Color.White) ){
        val (scrollList, bottomMenu) = createRefs()
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(scrollList){
                    top.linkTo(parent.top)
                    bottom.run {
                        linkTo(parent.bottom)
                    }
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }

        ){
            item{
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ){
                    Column {
                        Text("OnlineTechShop",
                            color = Color.Black,
                            fontSize = 24.sp)
                        Text(
                            userName, // <-- Sử dụng tên người dùng ở đây,
                            color = Color.Black,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row {
                        Image(
                            painter = painterResource(R.drawable.bell_icon),
                            contentDescription = "",
                        )
                        Spacer(modifier = Modifier.width(16.dp))

                        Image(
                            painter = painterResource(R.drawable.search_icon),
                            contentDescription = "Tìm kiếm",
                            modifier = Modifier.clickable {
                                showSearchDialog = true
                            }
                        )
                    }
                }
            }


            //Banners
            item {
                if(showBannerLoading){
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }else{
                    Banners(banners)
                }
            }
            item {
            SectionTitle("Loại Sản Phẩm", "Hiển Thị Tất Cả")
            }

            //Categories
            item{
                if(showCategoryLoading){
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }else{
                    CategoryList(categories)
                }
            }
            item {
                SectionTitle("Đề Xuất Sản Phẩm", "Hiển Thị Tất Cả")
            }

            //Recommended
            item{
                if(showRecommendedLoading){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }else{
                    ListItems(recommended)
                }
            }
            item{
                Spacer(modifier = Modifier.height(100.dp))
            }

        }
        // ✅ Gọi dialog ngay ngoài ConstraintLayout
        if (showSearchDialog) {
            SearchDialog(onDismiss = { showSearchDialog = false })
        }
        BottomMenu(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs (bottomMenu){
                    bottom.linkTo(anchor = parent.bottom)
                },
            onItemClick = onCartClick,
            onProfileClick = onProfileClick,
            onLookClick = onLookClick,
            onLoveClick = onLoveClick,
            onFollowClick = onFollowClick
        )

    }

}
@Composable
fun SearchDialog(
    onDismiss: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val allItems = remember { mutableStateListOf<ItemsModel>() }
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadAllItems()
        viewModel.allItems.observeForever {
            allItems.clear()
            allItems.addAll(it)
        }
    }

    val filtered = if (query.isBlank()) listOf()
    else allItems.filter { it.title.contains(query, ignoreCase = true) }

    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        // Không dùng fillMaxSize để tránh full màn
        Box(
            modifier = Modifier
                .fillMaxWidth()
                //.padding(horizontal = 16.dp)
        ){
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .heightIn(max = 500.dp)
                    .padding(top = 40.dp),
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // ✅ Thanh tìm kiếm trên cùng (không scroll)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = { Text("Tìm sản phẩm...") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            singleLine = true
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Đóng",
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ✅ Kết quả có thể scroll
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false) // chỉ chiếm chiều cao cần thiết
                    ) {
                        if (query.isNotBlank()) {
                            val results = filtered
                            if (results.isEmpty()) {
                                item {
                                    Text("❌ Không tìm thấy sản phẩm", color = Color.Red)
                                }
                            } else {
                                items(results) { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onDismiss()
                                                val intent = Intent(context, DetailActivity::class.java)
                                                intent.putExtra("object", item)
                                                context.startActivity(intent)
                                            }
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = item.picUrl.firstOrNull(),
                                            contentDescription = item.title,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Text(item.title, fontWeight = FontWeight.Bold)
                                            Text("💵 ${formatVND(item.price)}", fontSize = 12.sp, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        } else {
                            item {
                                Text("🔎 Hãy nhập từ khoá để tìm sản phẩm...", color = Color.Gray)
                            }
                        }
                    }
                }
            }

        }
    }
}



@Composable
fun CategoryList(categories: SnapshotStateList<CategoryModel>) {
    var selectedIndex by remember{ mutableStateOf(-1)}
    val context = LocalContext.current
    LazyRow ( modifier = Modifier
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp)

    ){

        items(categories.size){
            index ->
            CategoryItem( item = categories[index],
                isSelected = selectedIndex == index,
                onItemClick ={
                    selectedIndex = index
                    //Hiển thị
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(context, ListItemsActivity::class.java).apply {
                            putExtra("id",categories[index].id.toLong()) //id của mình là kiểu Long
                            putExtra("title",categories[index].title)
                        }
                        startActivity(context,intent,null)
                    },1000)
                })
        }
    }
}


//Danh sách loại sản phẩm
@Composable
fun CategoryItem(item:CategoryModel, isSelected:Boolean, onItemClick:() -> Unit) {
    Row (modifier = Modifier
        .clickable (onClick = onItemClick)
        .background(
            color = if(isSelected) colorResource(R.color.green) else Color.Transparent,
            shape = RoundedCornerShape(8.dp)
        ),
        verticalAlignment = Alignment.CenterVertically
    ){
        AsyncImage(
            model = (item.picUrl),
            contentDescription = item.title,

            modifier = Modifier
                .size(45.dp)
                .background(
                    color = if(isSelected) Color.Transparent else colorResource(R.color.lightGrey),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentScale = ContentScale.Inside,
            colorFilter = if(isSelected){
                ColorFilter.tint(Color.White)
            }else{
                ColorFilter.tint(Color.Black)
            }
        )
        if(isSelected){
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun Banners(banners:List<SliderModel>) {
    AutoSlidingCarousel(banners = banners)
}
@OptIn(ExperimentalPagerApi::class)
@Composable
fun AutoSlidingCarousel(modifier: Modifier = Modifier,
                        paperState: PagerState= remember { PagerState() },
                        banners: List<SliderModel>) {

    val isDragged by paperState.interactionSource.collectIsDraggedAsState()

    Column (modifier = modifier.fillMaxSize()){
        HorizontalPager(count = banners.size, state = paperState) {
            page ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(banners[page].url)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .padding(start = 16.dp,end = 16.dp, top = 16.dp, bottom = 8.dp)
                    .height(150.dp)

            )
        }
        DotIndicator(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .align(Alignment.CenterHorizontally),
            totalDots = banners.size,
            selectedIndex = if(isDragged) paperState.currentPage else paperState.currentPage,
            dotSize = 8.dp
        )
    }

}

@Composable
//Hiển thị một dãy các chấm tròn
fun DotIndicator(
    modifier: Modifier = Modifier,
    totalDots:Int,
    selectedIndex:Int,
    selectedColor: Color = colorResource(R.color.green),
    unSelectedColor: Color = colorResource(R.color.grey),
    dotSize: Dp
){
    LazyRow (
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
    ){
        items(totalDots){
            index ->
            IndicatorDot(
                color = if(index == selectedIndex)selectedColor else unSelectedColor,
                size = dotSize
            )

            if(index != totalDots-1){
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }

        }
    }
}

@Composable
fun IndicatorDot(modifier: Modifier = Modifier,
              size: Dp,
              color: Color)
{
    Box(modifier = modifier
        .size(size)
        .clip(CircleShape)
        .background(color)
    )

}

@Composable
fun SectionTitle(title:String, actionText:String){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = title,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = actionText,
            color = colorResource(R.color.green)
        )
    }
}
//Thanh điều hướng bên dưới
@Composable
fun BottomMenu(modifier: Modifier,onItemClick: () -> Unit,onProfileClick: () -> Unit, onLookClick: () -> Unit, onLoveClick: () -> Unit, onFollowClick: () -> Unit){
    Row (modifier = modifier
        .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
        .background(colorResource(R.color.green),
            shape = RoundedCornerShape(10.dp)
        ),
    horizontalArrangement = Arrangement.SpaceAround
    ){
        BottomMenuItem(icon = painterResource(R.drawable.btn_1), text = "Tìm Kiếm", onItemClick = onLookClick)
        BottomMenuItem(icon = painterResource(R.drawable.btn_2), text = "Giỏ Hàng", onItemClick = onItemClick)
        BottomMenuItem(icon = painterResource(R.drawable.btn_3), text = "Yêu Thích", onItemClick = onLoveClick)
        BottomMenuItem(icon = painterResource(R.drawable.btn_4), text = "Đơn Hàng", onItemClick = onFollowClick)
        BottomMenuItem(icon = painterResource(R.drawable.btn_5), text = "Tài Khoản", onItemClick = onProfileClick)
    }
}

@Composable
fun BottomMenuItem(icon:Painter, text: String, onItemClick: (() -> Unit)?= null){
    Column (
        modifier = Modifier
            .height(60.dp)
            .clickable{ onItemClick?.invoke() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Icon(icon, contentDescription = text, tint = Color.White)
        Text(text, color = Color.White, fontSize = 12.sp)
    }
}
