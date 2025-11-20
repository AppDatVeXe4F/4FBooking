package com.example.a4f.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import com.example.a4f.R
import com.example.a4f.data.DealItem
import com.example.a4f.data.FirestoreService
import com.example.a4f.data.NewsItem
import com.example.a4f.navigation.BottomNavItem
// import com.example.a4f.navigation.BottomNavItem // <-- Không cần dùng cái này nữa vì ta không switch tab
import com.example.a4f.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

// ... (Giữ nguyên phần Dữ liệu Dummy) ...
val imageList = listOf(R.drawable.image1, R.drawable.img_honphutu, R.drawable.image2)
val dummyDeals = listOf(
    DealItem(1, R.drawable.deal_vertical_dalat, "ĐÀ LẠT GỌI, BẠN TRẢ LỜI CHƯA?", "Tận hưởng không khí se lạnh..."),
    DealItem(2, R.drawable.deal_vertical_tet, "SĂN DEAL VÉ TẾT - VỀ NHÀ HẾT Ý!", "Lên kế hoạch về nhà ngay hôm nay...")
)
val dummyNews = listOf(
    NewsItem(1, R.drawable.news_traffic, "4F Bus sẽ đưa bạn vi vu trên những tuyến đường"),
    NewsItem(2, R.drawable.news_bus, "Trải nghiệm dòng xe bus cao cấp mới nhất")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    // Các biến State lưu dữ liệu người dùng chọn
    var diemDi by rememberSaveable { mutableStateOf("") }
    var diemDen by rememberSaveable { mutableStateOf("") }
    var ngayDi by rememberSaveable { mutableStateOf("28,Th9 2025") } // Giá trị mặc định

    var locations by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val datePickerState = rememberDatePickerState()
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val result = withContext(Dispatchers.IO) { FirestoreService.getLocationNames() }
            locations = if (result.isEmpty()) listOf("TP. Hồ Chí Minh", "An Giang", "Cần Thơ") else result
        } catch (e: Exception) {
            locations = listOf("Lỗi kết nối", "TP. Hồ Chí Minh", "An Giang")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = { HomeTopAppBar() },
        containerColor = LoginScreenBackground
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
                dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Hủy") } }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
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
                    locations = locations,
                    isLoading = isLoading,
                    navController = navController,
                    onSearchComplete = { }
                )
            }
            item { SectionHeader(title = "DEAL HỜI GIÁ TỐT") }
            items(dummyDeals) { deal -> HomeDealItem(deal = deal, onClick = {}) }
            item { NewsTitle() }
            items(dummyNews) { news -> NewsItemCard(news = news, onClick = {}) }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ... (Giữ nguyên HomeTopAppBar, ImagePagerSection) ...
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar() {
    CenterAlignedTopAppBar(
        title = { Text("4F", color = HomeSearchTitleColor, fontWeight = FontWeight.Bold, fontSize = 24.sp) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSection(
    modifier: Modifier = Modifier,
    diemDi: String,
    onDiemDiChange: (String) -> Unit,
    diemDen: String,
    onDiemDenChange: (String) -> Unit,
    ngayDi: String,
    onNgayDiClick: () -> Unit,
    locations: List<String>,
    isLoading: Boolean,
    navController: NavController,
    onSearchComplete: () -> Unit
) {
    var expandedDi by remember { mutableStateOf(false) }
    var expandedDen by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("CHÚNG TÔI CÓ THỂ ĐƯA BẠN ĐẾN ĐÂU ?", color = HomeSearchTitleColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Dropdown Điểm đi
            ExposedDropdownMenuBox(expanded = expandedDi, onExpandedChange = { expandedDi = it }) {
                OutlinedTextField(
                    value = diemDi, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth().menuAnchor(),
                    placeholder = { Text("Điểm đi") },
                    leadingIcon = { Icon(Icons.Default.DirectionsBus, null, tint = Color.Gray) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDi) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                        focusedContainerColor = HomeSearchInputBackground,
                        unfocusedContainerColor = HomeSearchInputBackground,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = expandedDi, onDismissRequest = { expandedDi = false }) {
                    locations.forEach { location ->
                        DropdownMenuItem(text = { Text(location) }, onClick = { onDiemDiChange(location); expandedDi = false })
                    }
                }
            }

            // Dropdown Điểm đến
            ExposedDropdownMenuBox(expanded = expandedDen, onExpandedChange = { expandedDen = it }) {
                OutlinedTextField(
                    value = diemDen, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth().menuAnchor(),
                    placeholder = { Text("Điểm đến") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Color.Gray) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDen) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                        focusedContainerColor = HomeSearchInputBackground,
                        unfocusedContainerColor = HomeSearchInputBackground,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = expandedDen, onDismissRequest = { expandedDen = false }) {
                    locations.forEach { location ->
                        DropdownMenuItem(text = { Text(location) }, onClick = { onDiemDenChange(location); expandedDen = false })
                    }
                }
            }

            SmallSearchInput(modifier = Modifier.fillMaxWidth().height(56.dp), icon = Icons.Default.CalendarMonth, text = ngayDi, onClick = onNgayDiClick)
        }

        Spacer(Modifier.height(24.dp))

        val isFormValid = diemDi.isNotBlank() && diemDen.isNotBlank()

        Button(
            onClick = {
                if (isFormValid) {
                    // 1. LƯU DỮ LIỆU VÀO BỘ NHỚ CỦA NAVIGATION
                    navController.currentBackStackEntry?.savedStateHandle?.set("source", diemDi)
                    navController.currentBackStackEntry?.savedStateHandle?.set("destination", diemDen)
                    navController.currentBackStackEntry?.savedStateHandle?.set("date", ngayDi)

                    // 2. CHUYỂN SANG TAB ĐẶT VÉ
                    navController.navigate(BottomNavItem.Booking.route) {
                        // Xóa stack để tránh bị back vòng vo
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid) LoginButtonColor else Color.LightGray
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Đi thôi!!!!", fontSize = 18.sp, color = if (isFormValid) Color.White else Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}


@Composable
fun SmallSearchInput(modifier: Modifier = Modifier, icon: ImageVector?, text: String, onClick: () -> Unit) {
    Row(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(HomeSearchInputBackground).clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(it, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
        }
        Text(text, color = Color.DarkGray, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SectionHeader(title: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).background(HomeSectionHeaderGray).padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(title, color = HomeSearchTitleColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun HomeDealItem(deal: DealItem, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp).clickable(onClick = onClick)) {
        Image(painter = painterResource(deal.imageRes), contentDescription = null, contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)))
        Spacer(Modifier.height(16.dp))
        Text(deal.title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = HomeSearchTitleColor)
        Spacer(Modifier.height(8.dp))
        Text(deal.description, fontSize = 14.sp, color = Color.Gray, lineHeight = 21.sp)
    }
}

@Composable
fun NewsTitle() {
    Text(
        "News",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        color = NewsTitleColor,
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
    )
}

@Composable
fun NewsItemCard(news: NewsItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Image(
                painter = painterResource(news.imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().aspectRatio(1.7f)
            )
            Text(
                news.title,
                fontWeight = FontWeight.Bold,
                color = HomeSearchTitleColor,
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

private fun convertMillisToDateString(millis: Long): String {
    val sdf = SimpleDateFormat("dd,'Th'M yyyy", Locale("vi", "VN"))
    return sdf.format(Date(millis))
}