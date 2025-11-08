package com.example.a4f.screens

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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import com.example.a4f.R
import com.example.a4f.data.DealItem
import com.example.a4f.data.NewsItem
import com.example.a4f.ui.theme.*

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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

// Danh sách Deal (cuộn dọc)
val dummyDeals = listOf(
    DealItem(1,
        R.drawable.deal_vertical_dalat,
        "ĐÀ LẠT GỌI, BẠN TRẢ LỜI CHƯA?",
        "Tận hưởng không khí se lạnh... Check-in 'tiểu Paris' ngay!"
    ),
    DealItem(2,
        R.drawable.deal_vertical_tet,
        "SĂN DEAL VÉ TẾT - VỀ NHÀ HẾT Ý!",
        "Lên kế hoạch về nhà ngay hôm nay... An tâm về quê, vui Tết sum họp."
    )
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
    var soNguoi by rememberSaveable { mutableStateOf(1) }
    var loaiVe by rememberSaveable { mutableStateOf("Một chiều") }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    var showSoNguoiDialog by rememberSaveable { mutableStateOf(false) }
    var tempSoNguoi by remember { mutableStateOf(soNguoi) }

    var showLoaiVeDialog by rememberSaveable { mutableStateOf(false) }
    var tempLoaiVe by remember { mutableStateOf(loaiVe) }

    var selectedBottomItem by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { HomeTopAppBar() },
        containerColor = LoginScreenBackground,
        bottomBar = {
            MyBottomNavBar(
                selectedItem = selectedBottomItem,
                onItemSelected = { selectedBottomItem = it }
            )
        }
    ) { paddingValues ->

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                            datePickerState.selectedDateMillis?.let { millis ->
                                ngayDi = convertMillisToDateString(millis)
                            }
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Hủy") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showSoNguoiDialog) {
            AlertDialog(
                onDismissRequest = { showSoNguoiDialog = false },
                title = { Text("Chọn số lượng người") },
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(onClick = { if (tempSoNguoi > 1) tempSoNguoi-- }) {
                            Icon(Icons.Default.Remove, contentDescription = "Giảm")
                        }
                        Text(
                            text = "$tempSoNguoi",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        IconButton(onClick = { tempSoNguoi++ }) {
                            Icon(Icons.Default.Add, contentDescription = "Tăng")
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            soNguoi = tempSoNguoi
                            showSoNguoiDialog = false
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showSoNguoiDialog = false }) { Text("Hủy") }
                }
            )
        }

        if (showLoaiVeDialog) {
            val options = listOf("Một chiều", "Hai chiều")
            AlertDialog(
                onDismissRequest = { showLoaiVeDialog = false },
                title = { Text("Chọn loại vé") },
                text = {
                    Column(Modifier.fillMaxWidth()) {
                        options.forEach { text ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (text == tempLoaiVe),
                                        onClick = { tempLoaiVe = text }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (text == tempLoaiVe),
                                    onClick = { tempLoaiVe = text }
                                )
                                Text(text = text, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            loaiVe = tempLoaiVe
                            showLoaiVeDialog = false
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showLoaiVeDialog = false }) { Text("Hủy") }
                }
            )
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
                    diemDi = diemDi, onDiemDiChange = { diemDi = it },
                    diemDen = diemDen, onDiemDenChange = { diemDen = it },
                    ngayDi = ngayDi, soNguoi = soNguoi.toString(), loaiVe = loaiVe,
                    onNgayDiClick = { showDatePicker = true },
                    onSoNguoiClick = {
                        tempSoNguoi = soNguoi
                        showSoNguoiDialog = true
                    },
                    onLoaiVeClick = {
                        tempLoaiVe = loaiVe
                        showLoaiVeDialog = true
                    }
                )
            }

            item { SectionHeader(title = "DEAL HỜI GIÁ TỐT") }

            items(dummyDeals) { deal ->
                HomeDealItem(
                    deal = deal,
                    onClick = {
                        // TODO: Xử lý khi nhấn vào deal
                    }
                )
            }

            item {
                NewsTitle()
            }

            items(dummyNews) { news ->
                NewsItemCard(
                    news = news,
                    onClick = {
                        // TODO: Xử lý khi nhấn vào tin tức
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// -----------------------------------------------------------------
// --- CÁC HÀM HỖ TRỢ (Nằm ngoài HomeScreen) ---
// -----------------------------------------------------------------

// 1. HomeTopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "4F",
                color = HomeSearchTitleColor,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = HomeTopBarBackground
        )
    )
}

// 2. ImagePagerSection
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePagerSection() {
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { imageList.size }
    )
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentPadding = PaddingValues(horizontal = 64.dp)
    ) { page ->
        val pageOffset = (
                (pagerState.currentPage - page) + pagerState
                    .currentPageOffsetFraction
                ).absoluteValue
        val scale = lerp(
            start = 0.85f,
            stop = 1f,
            fraction = 1f - pageOffset.coerceIn(0f, 1f)
        )
        Image(
            painter = painterResource(id = imageList[page]),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .clip(RoundedCornerShape(16.dp))
        )
    }
}

// 3. Khối tìm kiếm
@Composable
fun SearchSection(
    modifier: Modifier = Modifier,
    diemDi: String,
    onDiemDiChange: (String) -> Unit,
    diemDen: String,
    onDiemDenChange: (String) -> Unit,
    ngayDi: String,
    soNguoi: String,
    loaiVe: String,
    onNgayDiClick: () -> Unit,
    onSoNguoiClick: () -> Unit,
    onLoaiVeClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
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
            OutlinedTextField(
                value = diemDi,
                onValueChange = onDiemDiChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Điểm đi") },
                leadingIcon = { Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = Color.Gray) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = HomeSearchInputBackground,
                    unfocusedContainerColor = HomeSearchInputBackground,
                    disabledContainerColor = HomeSearchInputBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedLeadingIconColor = HomeSearchTitleColor,
                    unfocusedLeadingIconColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = diemDen,
                onValueChange = onDiemDenChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Điểm đến") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = HomeSearchInputBackground,
                    unfocusedContainerColor = HomeSearchInputBackground,
                    disabledContainerColor = HomeSearchInputBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedLeadingIconColor = HomeSearchTitleColor,
                    unfocusedLeadingIconColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallSearchInput(
                    modifier = Modifier.weight(1.5f),
                    icon = Icons.Default.CalendarMonth,
                    text = ngayDi,
                    onClick = onNgayDiClick
                )
                SmallSearchInput(
                    modifier = Modifier.weight(0.8f),
                    icon = Icons.Default.Person,
                    text = soNguoi,
                    onClick = onSoNguoiClick
                )
                SmallSearchInput(
                    modifier = Modifier.weight(1.5f),
                    icon = null,
                    text = loaiVe,
                    onClick = onLoaiVeClick
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { /* TODO: Xử lý tìm kiếm */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LoginButtonColor),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = "Đi thôi!!!!", fontSize = 18.sp, color = Color.White)
        }
    }
}

// 4. Composable cho 3 ô nhỏ
@Composable
fun SmallSearchInput(
    modifier: Modifier = Modifier,
    icon: ImageVector?,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(HomeSearchInputBackground)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, color = Color.DarkGray, fontSize = 14.sp)
    }
}

// 5. Composable cho thanh tiêu đề "DEAL HỜI..."
@Composable
fun SectionHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .background(HomeSectionHeaderGray)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            color = HomeSearchTitleColor,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

// 6. Composable cho một thẻ Deal (cuộn dọc)
@Composable
fun HomeDealItem(
    deal: DealItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = deal.imageRes),
            contentDescription = deal.title,
            contentScale = ContentScale.Fit, // Giữ nguyên ảnh, không cắt
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = deal.title,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = HomeSearchTitleColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = deal.description,
            fontSize = 14.sp,
            color = Color.Gray,
            lineHeight = 21.sp
        )
    }
}

// 7. Composable cho chữ "News"
@Composable
fun NewsTitle() {
    Text(
        text = "News",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        color = NewsTitleColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
    )
}

// 8. Composable cho một thẻ Tin tức
@Composable
fun NewsItemCard(
    news: NewsItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Image(
                painter = painterResource(id = news.imageRes),
                contentDescription = news.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.7f)
            )
            Text(
                text = news.title,
                fontWeight = FontWeight.Bold,
                color = HomeSearchTitleColor,
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

// 9. Composable cho Thanh Điều hướng Dưới cùng (Bottom Nav Bar)
@Composable
fun MyBottomNavBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = BottomNavSelected
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    if (selectedItem == 0) Icons.Default.Home else Icons.Outlined.Home,
                    contentDescription = "Trang chủ"
                )
            },
            label = { Text("Trang chủ") },
            selected = selectedItem == 0,
            onClick = { onItemSelected(0) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BottomNavSelected,
                selectedTextColor = BottomNavSelected,
                unselectedIconColor = BottomNavUnselected,
                unselectedTextColor = BottomNavUnselected
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Đặt vé") },
            label = { Text("Đặt vé") },
            selected = selectedItem == 1,
            onClick = { onItemSelected(1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BottomNavSelected,
                selectedTextColor = BottomNavSelected,
                unselectedIconColor = BottomNavUnselected,
                unselectedTextColor = BottomNavUnselected
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    if (selectedItem == 2) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Vé của tôi"
                )
            },
            label = { Text("Vé của tôi") },
            selected = selectedItem == 2,
            onClick = { onItemSelected(2) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BottomNavSelected,
                selectedTextColor = BottomNavSelected,
                unselectedIconColor = BottomNavUnselected,
                unselectedTextColor = BottomNavUnselected
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    if (selectedItem == 3) Icons.Default.Person else Icons.Outlined.Person,
                    contentDescription = "Cá nhân"
                )
            },
            label = { Text("Cá nhân") },
            selected = selectedItem == 3,
            onClick = { onItemSelected(3) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BottomNavSelected,
                selectedTextColor = BottomNavSelected,
                unselectedIconColor = BottomNavUnselected,
                unselectedTextColor = BottomNavUnselected
            )
        )
    }
}

// 10. Hàm hỗ trợ đổi ngày
private fun convertMillisToDateString(millis: Long): String {
    val sdf = SimpleDateFormat("dd,'Th'M yyyy", Locale("vi", "VN"))
    return sdf.format(Date(millis))
}