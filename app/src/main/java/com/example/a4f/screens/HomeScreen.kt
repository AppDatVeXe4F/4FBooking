package com.example.a4f.screens

import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import com.example.a4f.data.FirestoreService
import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import com.example.a4f.R
import com.example.a4f.data.DealItem
import com.example.a4f.data.NewsItem
import com.example.a4f.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

// Danh sách ảnh Pager
val imageList = listOf(
    R.drawable.image1,
    R.drawable.img_honphutu,
    R.drawable.image2
)

// Danh sách Deal
val dummyDeals = listOf(
    DealItem(1, R.drawable.deal_vertical_dalat, "ĐÀ LẠT GỌI, BẠN TRẢ LỜI CHƯA?", "Tận hưởng không khí se lạnh... Check-in 'tiểu Paris' ngay!"),
    DealItem(2, R.drawable.deal_vertical_tet, "SĂN DEAL VÉ TẾT - VỀ NHÀ HẾT Ý!", "Lên kế hoạch về nhà ngay hôm nay... An tâm về quê, vui Tết sum họp.")
)

// Danh sách Tin Tức
val dummyNews = listOf(
    NewsItem(1, R.drawable.news_traffic, "4F Bus sẽ đưa bạn vi vu trên những tuyến đường"),
    NewsItem(2, R.drawable.news_bus, "Trải nghiệm dòng xe bus cao cấp mới nhất")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    var diemDi by rememberSaveable { mutableStateOf("") }
    var diemDen by rememberSaveable { mutableStateOf("") }
    var ngayDi by rememberSaveable { mutableStateOf("28,Th9 2025") }
    var isSearchComplete by rememberSaveable { mutableStateOf(false) }

    var locations by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var selectedBottomItem by remember { mutableStateOf(0) }

    // DÙNG FIRESTORE THAY REALTIME DB
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            locations = FirestoreService.getLocationNames()
        }
        isLoading = false
    }

    Scaffold(
        topBar = { HomeTopAppBar() },
        containerColor = LoginScreenBackground,
        bottomBar = {
            MyBottomNavBar(
                selectedItem = selectedBottomItem,
                onItemSelected = { selectedBottomItem = it },
                isSearchComplete = isSearchComplete,
                onSearchComplete = { isSearchComplete = true }
            )
        }
    ) { paddingValues ->
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            ngayDi = convertMillisToDateString(millis)
                        }
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Hủy") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { ImagePagerSection() }
            item {
                SearchSection(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    diemDi = diemDi,
                    onDiemDiChange = { diemDi = it },
                    diemDen = diemDen,
                    onDiemDenChange = { diemDen = it },
                    ngayDi = ngayDi,
                    onNgayDiClick = { showDatePicker = true },
                    locations = locations,        // ĐÃ THÊM
                    isLoading = isLoading,        // ĐÃ THÊM
                    navController = navController,
                    onSearchComplete = { isSearchComplete = true }
                )
            }
            item { SectionHeader(title = "DEAL HỜI GIÁ TỐT") }
            items(dummyDeals) { deal ->
                HomeDealItem(deal = deal, onClick = { /* TODO */ })
            }
            item { NewsTitle() }
            items(dummyNews) { news ->
                NewsItemCard(news = news, onClick = { /* TODO */ })
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// -----------------------------------------------------------------
// --- CÁC HÀM HỖ TRỢ ---
// -----------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Text("4F", color = HomeSearchTitleColor, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = HomeTopBarBackground)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePagerSection() {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { imageList.size })
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        contentPadding = PaddingValues(horizontal = 64.dp)
    ) { page ->
        val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
        val scale = lerp(0.85f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
        Image(
            painter = painterResource(id = imageList[page]),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .clip(RoundedCornerShape(16.dp))
        )
    }
}

// CHỈNH SỬA: XÓA 2 NÚT, NGÀY ĐI TO HƠN
@Composable
fun SearchSection(
    modifier: Modifier = Modifier,
    diemDi: String,
    onDiemDiChange: (String) -> Unit,
    diemDen: String,
    onDiemDenChange: (String) -> Unit,
    ngayDi: String,
    onNgayDiClick: () -> Unit,
    locations: List<String>,      // ĐÃ THÊM
    isLoading: Boolean,           // ĐÃ THÊM
    navController: NavController,
    onSearchComplete: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CHÚNG TÔI CÓ THỂ ĐƯA BẠN ĐẾN ĐÂU ?",
            color = HomeSearchTitleColor,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ĐIỂM ĐI
            OutlinedTextField(
                value = diemDi,
                onValueChange = onDiemDiChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Điểm đi") },
                leadingIcon = { Icon(Icons.Default.DirectionsBus, null, tint = Color.Gray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = HomeSearchInputBackground,
                    unfocusedContainerColor = HomeSearchInputBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedLeadingIconColor = HomeSearchTitleColor,
                    unfocusedLeadingIconColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // ĐIỂM ĐẾN
            OutlinedTextField(
                value = diemDen,
                onValueChange = onDiemDenChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Điểm đến") },
                leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Color.Gray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = HomeSearchInputBackground,
                    unfocusedContainerColor = HomeSearchInputBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedLeadingIconColor = HomeSearchTitleColor,
                    unfocusedLeadingIconColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // NGÀY ĐI – TO HƠN, ĐẸP HƠN, CHIẾM TOÀN BỘ
            SmallSearchInput(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                icon = Icons.Default.CalendarMonth,
                text = ngayDi,
                onClick = onNgayDiClick
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // NÚT "ĐI THÔI!!!"
        val isFormValid = diemDi.isNotBlank() && diemDen.isNotBlank()

        Button(
            onClick = {
                if (isFormValid) {
                    onSearchComplete()
                    navController.navigate("booking_screen")
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid) LoginButtonColor else Color.LightGray
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Đi thôi!!!!", fontSize = 18.sp, color = if (isFormValid) Color.White else Color.Gray)
        }
    }
}

// NÚT NGÀY ĐI – ĐẸP, TO, CHIẾM HẾT
@Composable
fun SmallSearchInput(
    modifier: Modifier = Modifier,
    icon: ImageVector?,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(HomeSearchInputBackground)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
        }
        Text(text = text, color = Color.DarkGray, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

// Giữ nguyên các hàm khác
@Composable
fun SectionHeader(title: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).background(HomeSectionHeaderGray)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = title, color = HomeSearchTitleColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun HomeDealItem(deal: DealItem, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp).clickable { onClick() }) {
        Image(painter = painterResource(id = deal.imageRes), contentDescription = null, contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)))
        Spacer(modifier = Modifier.height(16.dp))
        Text(deal.title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = HomeSearchTitleColor)
        Spacer(modifier = Modifier.height(8.dp))
        Text(deal.description, fontSize = 14.sp, color = Color.Gray, lineHeight = 21.sp)
    }
}

@Composable
fun NewsTitle() {
    Text("News", fontSize = 22.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic, color = NewsTitleColor,
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 8.dp, start = 16.dp, end = 16.dp))
}

@Composable
fun NewsItemCard(news: NewsItem, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column {
            Image(painter = painterResource(id = news.imageRes), contentDescription = null, contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().aspectRatio(1.7f))
            Text(news.title, fontWeight = FontWeight.Bold, color = HomeSearchTitleColor, fontSize = 16.sp,
                modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun MyBottomNavBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    isSearchComplete: Boolean,
    onSearchComplete: () -> Unit
) {
    NavigationBar(containerColor = Color.White, contentColor = BottomNavSelected) {
        NavigationBarItem(
            icon = { Icon(if (selectedItem == 0) Icons.Default.Home else Icons.Outlined.Home, "Trang chủ") },
            label = { Text("Trang chủ") },
            selected = selectedItem == 0,
            onClick = { onItemSelected(0) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BottomNavSelected, selectedTextColor = BottomNavSelected,
                unselectedIconColor = BottomNavUnselected, unselectedTextColor = BottomNavUnselected
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.CalendarMonth, "Đặt vé") },
            label = { Text("Đặt vé") },
            selected = selectedItem == 1,
            onClick = { if (isSearchComplete) onItemSelected(1) },
            enabled = isSearchComplete,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BottomNavSelected, selectedTextColor = BottomNavSelected,
                unselectedIconColor = if (isSearchComplete) BottomNavUnselected else Color.LightGray,
                unselectedTextColor = if (isSearchComplete) BottomNavUnselected else Color.LightGray
            )
        )

        NavigationBarItem(
            icon = { Icon(if (selectedItem == 2) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder, "Vé của tôi") },
            label = { Text("Vé của tôi") },
            selected = selectedItem == 2,
            onClick = { onItemSelected(2) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BottomNavSelected, selectedTextColor = BottomNavSelected,
                unselectedIconColor = BottomNavUnselected, unselectedTextColor = BottomNavUnselected
            )
        )

        NavigationBarItem(
            icon = { Icon(if (selectedItem == 3) Icons.Default.Person else Icons.Outlined.Person, "Cá nhân") },
            label = { Text("Cá nhân") },
            selected = selectedItem == 3,
            onClick = { onItemSelected(3) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BottomNavSelected, selectedTextColor = BottomNavSelected,
                unselectedIconColor = BottomNavUnselected, unselectedTextColor = BottomNavUnselected
            )
        )
    }
}

private fun convertMillisToDateString(millis: Long): String {
    val sdf = SimpleDateFormat("dd,'Th'M yyyy", Locale("vi", "VN"))
    return sdf.format(Date(millis))
}