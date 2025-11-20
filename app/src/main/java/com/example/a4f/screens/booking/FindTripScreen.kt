package com.example.a4f.screens.booking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a4f.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// --- MÀU SẮC ---
val AppGreen = Color(0xFF4A7873)
val AppLightGreen = Color(0xFFB8D0CD)
val AppWhite = Color.White
val AppDarkGray = Color(0xFF424242)
val AppLightGrayBg = Color(0xFFEEEEEE)
val AppGrayText = Color(0xFF757575)
val AppFilterGreen = Color(0xFF49736E)
val AppGrayBg = Color(0xFFF0F0F0)

// --- DATA MODEL ---
data class Trip(
    val id: String,
    val startTime: String,
    val startStation: String,
    val endTime: String,
    val endStation: String,
    val distanceTime: String,
    val price: String,
    val seatsAvailable: Int,
    val seatType: String // Limousine, Giường nằm, Ghế ngồi
) {
    val realPrice: Int
        get() = price.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0
    val startHour: Int
        get() = startTime.split(":")[0].toIntOrNull() ?: 0
}

@Composable
fun FindTripScreen(
    navController: NavController,
    source: String?,
    destination: String?,
    date: String? // Định dạng nhận vào: "28,Th9 2025"
) {
    // --- 1. XỬ LÝ LOGIC NGÀY THÁNG ---
    // Tạo danh sách 7 ngày bắt đầu từ ngày được truyền vào
    val dateListObj = remember(date) { generateNext7Days(date) }

    // State lưu chỉ số ngày đang chọn (Mặc định là 0 - ngày đầu tiên)
    var selectedDateIndex by remember { mutableIntStateOf(0) }

    // Lấy chuỗi ngày đầy đủ để hiển thị trên TopBar (Ví dụ: Thứ Hai, 21/11/2025)
    val currentTopBarDate = dateListObj.getOrNull(selectedDateIndex)?.fullDateString ?: "Đang tải..."

    // --- 2. TẠO DỮ LIỆU GIẢ LẬP (6 CHUYẾN/NGÀY, KHÁC NHAU THEO NGÀY) ---
    // Khi selectedDateIndex thay đổi, danh sách chuyến xe sẽ được tạo mới
    val allTrips = remember(selectedDateIndex) {
        generateMockTripsForDate(selectedDateIndex, source, destination)
    }

    // --- 3. LOGIC LỌC (FILTER) ---
    var sortOption by remember { mutableStateOf("Mặc định") }
    var seatFilter by remember { mutableStateOf("Tất cả") }
    var timeFilter by remember { mutableStateOf("Tất cả") }

    val filteredTrips = remember(allTrips, sortOption, seatFilter, timeFilter) {
        var result = allTrips

        if (seatFilter != "Tất cả") {
            result = result.filter { it.seatType == seatFilter }
        }
        if (timeFilter != "Tất cả") {
            result = result.filter { trip ->
                val h = trip.startHour
                when (timeFilter) {
                    "Buổi sáng (06:00 - 12:00)" -> h in 6..11
                    "Buổi chiều (12:00 - 18:00)" -> h in 12..17
                    "Buổi tối (18:00 - 22:00)" -> h in 18..22
                    else -> true
                }
            }
        }
        when (sortOption) {
            "Tăng dần" -> result.sortedBy { it.realPrice }
            "Giảm dần" -> result.sortedByDescending { it.realPrice }
            else -> result
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(AppGreen)) {
        // --- HEADER ---
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = AppWhite)
                }
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${source?.uppercase() ?: "BẾN XE MIỀN TÂY"} → ${destination?.uppercase() ?: "VP LONG XUYÊN"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppWhite
                    )
                    // HIỂN THỊ NGÀY ĐỘNG THEO LỰA CHỌN
                    Text(
                        text = currentTopBarDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppWhite.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // --- BODY ---
        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            // DANH SÁCH NGÀY (HORIZONTAL SCROLL)
            LazyRow(
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dateListObj.size) { index ->
                    val item = dateListObj[index]
                    DateItem(
                        dayOfWeek = item.dayOfWeek,
                        dateMonth = item.shortDate,
                        isSelected = index == selectedDateIndex,
                        onClick = { selectedDateIndex = index }
                    )
                }
            }

            BookingStepperCustom()

            FilterBar(
                currentSort = sortOption, onSortSelected = { sortOption = it },
                currentSeat = seatFilter, onSeatSelected = { seatFilter = it },
                currentTime = timeFilter, onTimeSelected = { timeFilter = it }
            )

            // DANH SÁCH CHUYẾN XE
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (filteredTrips.isEmpty()) {
                    item {
                        Text("Không có chuyến nào!", modifier = Modifier.fillMaxWidth().padding(20.dp), textAlign = TextAlign.Center, color = Color.Gray)
                    }
                } else {
                    items(filteredTrips) { trip ->
                        TripCardItem(trip = trip, onClick = {
                            val priceInt = trip.realPrice
                            val src = source ?: "TP. HCM"
                            val dest = destination ?: "AN GIANG"
                            val dateStr = currentTopBarDate.replace("/", "-")
                            val tripId = trip.id

                            // LẤY GIỜ CHẠY (Ví dụ: 06:15)
                            val time = trip.startTime

                            // GỬI THÊM $time VÀO CUỐI ĐƯỜNG DẪN
                            navController.navigate("select_seat_screen/$tripId/$priceInt/$src/$dest/$dateStr/$time")
                        })
                    }
                }
            }
        }
    }
}

// --- HÀM TẠO DỮ LIỆU GIẢ LẬP (6 Chuyến/Ngày) ---
fun generateMockTripsForDate(index: Int, source: String?, dest: String?): List<Trip> {
    // Logic: Dựa vào index (ngày thứ mấy) để thay đổi giờ chạy một chút cho khác nhau
    // 3 trạm yêu cầu: Bến xe Miền Tây, Bến xe Hà Tiên, VP Long Xuyên

    // Xác định trạm đi/đến dựa vào tham số hoặc mặc định
    val start = source ?: "Bến xe Miền Tây"
    val destination = null
    val end = destination ?: "VP Long Xuyên"

    // Tạo biến đổi giờ để mỗi ngày giờ chạy lệch nhau 1 chút (cho cảm giác dữ liệu thật)
    val offset = index * 15 // Mỗi ngày lệch 15 phút

    return listOf(
        Trip("1", "${6 + (index % 2)}:${15 + (index * 5) % 45}", start, "${10 + (index % 2)}:${15 + (index * 5) % 45}", end, "185km - 4h", "230.000đ", 20 - index, "Limousine"),
        Trip("2", "${8 + (index % 2)}:00", start, "${12 + (index % 2)}:00", end, "185km - 4h", "230.000đ", 15 + index, "Limousine"),
        Trip("3", "${10 + (index % 2)}:30", start, "${14 + (index % 2)}:30", end, "185km - 4h", "200.000đ", 5 + index, "Giường nằm"),
        Trip("4", "13:${15 + offset % 45}", start, "17:${15 + offset % 45}", end, "185km - 4h", "200.000đ", 12, "Giường nằm"),
        Trip("5", "15:00", start, "19:00", end, "185km - 4h", "230.000đ", 8, "Limousine"),
        Trip("6", "22:00", start, "02:00", end, "185km - 4h", "200.000đ", 30, "Giường nằm")
    )
}

// --- HÀM XỬ LÝ NGÀY THÁNG ---
data class DateUIModel(
    val dayOfWeek: String, // "Th 2"
    val shortDate: String, // "29/09"
    val fullDateString: String // "Thứ Hai, 29/09/2025" (Cho TopBar)
)

fun generateNext7Days(startDateString: String?): List<DateUIModel> {
    val list = mutableListOf<DateUIModel>()
    val inputFormat = SimpleDateFormat("dd,'Th'M yyyy", Locale("vi", "VN")) // Format từ Home
    val cal = Calendar.getInstance()

    try {
        if (!startDateString.isNullOrBlank()) {
            cal.time = inputFormat.parse(startDateString) ?: Date()
        }
    } catch (e: Exception) {
        cal.time = Date() // Fallback về ngày hiện tại nếu lỗi
    }

    val dayOfWeekFormat = SimpleDateFormat("EEE", Locale("vi", "VN")) // "Th 2"
    val shortDateFormat = SimpleDateFormat("dd/MM", Locale("vi", "VN")) // "29/09"
    val fullDateFormat = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale("vi", "VN")) // TopBar

    for (i in 0 until 7) {
        var dayName = dayOfWeekFormat.format(cal.time).replace("Th ", "Th ")
        if (dayName.contains("CN") || dayName.contains("Chủ Nhật")) dayName = "CN"

        // Chỉnh lại tên thứ cho giống hình (Th 2, Th 3...)
        if (dayName.startsWith("t")) dayName = dayName.replace("t", "Th ")
        if (dayName.startsWith("T")) dayName = dayName.replace("T", "Th ").replace("Th h", "Th ")

        // Fix cứng cho đẹp nếu SimpleDateFormat ra kết quả lạ
        val dayOfWeekNum = cal.get(Calendar.DAY_OF_WEEK)
        val finalDayName = when(dayOfWeekNum) {
            Calendar.SUNDAY -> "CN"
            Calendar.MONDAY -> "Th 2"
            Calendar.TUESDAY -> "Th 3"
            Calendar.WEDNESDAY -> "Th 4"
            Calendar.THURSDAY -> "Th 5"
            Calendar.FRIDAY -> "Th 6"
            Calendar.SATURDAY -> "Th 7"
            else -> dayName
        }

        list.add(
            DateUIModel(
                dayOfWeek = finalDayName,
                shortDate = shortDateFormat.format(cal.time),
                fullDateString = fullDateFormat.format(cal.time).replaceFirstChar { it.uppercase() }
            )
        )
        cal.add(Calendar.DAY_OF_MONTH, 1) // Tăng thêm 1 ngày
    }
    return list
}

// --- UI COMPONENTS ---

@Composable
fun DateItem(dayOfWeek: String, dateMonth: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = if (isSelected) AppGreen else Color(0xFFEFF5F4)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.height(65.dp).width(60.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = dayOfWeek, textAlign = TextAlign.Center, color = if (isSelected) Color.White else AppGreen, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = dateMonth, textAlign = TextAlign.Center, color = if (isSelected) Color.White else AppGreen, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
fun BookingStepperCustom() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Surface(color = AppGreen, shape = RoundedCornerShape(20.dp), modifier = Modifier.height(32.dp)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(text = "THỜI GIAN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppWhite)
                }
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = AppDarkGray, modifier = Modifier.size(20.dp))
            Text("Chọn ghế", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Thông tin", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Thanh toán", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(AppGreen))
            Divider(modifier = Modifier.weight(1f).height(1.dp), color = AppGreen)
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AppLightGreen))
            Divider(modifier = Modifier.weight(1f).height(1.dp), color = AppGreen)
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AppLightGreen))
            Divider(modifier = Modifier.weight(1f).height(1.dp), color = AppGreen)
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AppLightGreen))
        }
    }
}

@Composable
fun FilterBar(
    currentSort: String, onSortSelected: (String) -> Unit,
    currentSeat: String, onSeatSelected: (String) -> Unit,
    currentTime: String, onTimeSelected: (String) -> Unit
) {
    var expandedType by remember { mutableStateOf<String?>(null) }
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FilterChipWithMenu(if (currentSort == "Mặc định") "Giá" else "Giá...", listOf("Mặc định", "Tăng dần", "Giảm ần"), expandedType == "PRICE", currentSort != "Mặc định", { expandedType = if (it) "PRICE" else null }, { onSortSelected(it); expandedType = null })
        FilterChipWithMenu(if (currentSeat == "Tất cả") "Loại ghế" else currentSeat, listOf("Tất cả", "Giường nằm", "Limousine"), expandedType == "SEAT", currentSeat != "Tất cả", { expandedType = if (it) "SEAT" else null }, { onSeatSelected(it); expandedType = null })
        FilterChipWithMenu(if (currentTime == "Tất cả") "Giờ" else "Đã chọn", listOf("Tất cả", "Buổi sáng (06:00 - 12:00)", "Buổi chiều (12:00 - 18:00)", "Buổi tối (18:00 - 22:00)"), expandedType == "TIME", currentTime != "Tất cả", { expandedType = if (it) "TIME" else null }, { onTimeSelected(it); expandedType = null })
    }
}

@Composable
fun FilterChipWithMenu(label: String, options: List<String>, isExpanded: Boolean, isActive: Boolean, onExpandChange: (Boolean) -> Unit, onOptionSelected: (String) -> Unit) {
    Box {
        Surface(shape = RoundedCornerShape(8.dp), color = if (isExpanded || isActive) AppFilterGreen else AppGrayBg, modifier = Modifier.height(36.dp).clickable { onExpandChange(!isExpanded) }) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(text = label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (isExpanded || isActive) Color.White else AppDarkGray)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (isExpanded || isActive) Color.White else AppDarkGray)
            }
        }
        DropdownMenu(expanded = isExpanded, onDismissRequest = { onExpandChange(false) }, modifier = Modifier.background(Color.White)) {
            options.forEach { option -> DropdownMenuItem(text = { Text(text = option, fontSize = 14.sp) }, onClick = { onOptionSelected(option) }) }
        }
    }
}

@Composable
fun TripCardItem(trip: Trip, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp), // Tăng chiều cao lên một chút cho thoáng
        shape = RoundedCornerShape(24.dp), // Bo góc tròn trịa như hình mẫu
        colors = CardDefaults.cardColors(containerColor = AppGreen), // Nền xanh đậm
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // --- TẦNG 1: GIỜ ĐI - GIỜ ĐẾN ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trip.startTime,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppWhite
                )
                Text(
                    text = trip.endTime,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFE0E0) // Màu hơi hồng nhạt giống hình mẫu
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- TẦNG 2: THÔNG TIN BẾN & ẢNH XE ---
            Row(
                modifier = Modifier
                    .weight(1f) // Chiếm toàn bộ khoảng trống ở giữa
                    .fillMaxWidth()
            ) {
                // CỘT TRÁI: Timeline + Tên bến
                Row(modifier = Modifier.weight(1.1f)) {
                    // 1. Timeline (Cây dọc nối 2 điểm)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(top = 6.dp) // Căn chỉnh cho ngang với dòng chữ đầu
                            .width(16.dp)
                    ) {
                        // Vòng tròn đi (Dùng Box vẽ cho chuẩn)
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color.Transparent)
                                .border(1.5.dp, AppDarkGray, CircleShape)
                        )
                        // Đường kẻ dọc
                        Box(
                            modifier = Modifier
                                .width(1.5.dp)
                                .weight(1f) // Tự động kéo dài
                                .background(AppDarkGray)
                        )
                        // Vòng tròn đến
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color.Transparent)
                                .border(1.5.dp, AppDarkGray, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // 2. Tên bến + Khoảng cách
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        // Bến đi
                        Text(
                            text = trip.startStation,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppWhite
                        )

                        // Khoảng cách (Nằm giữa)
                        Text(
                            text = "Khoảng cách: ${trip.distanceTime}",
                            fontSize = 11.sp,
                            color = AppWhite.copy(alpha = 0.8f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        // Bến đến
                        Text(
                            text = trip.endStation,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppWhite
                        )
                    }
                }

                // CỘT PHẢI: Ảnh xe Bus
                // Dùng Box để căn chỉnh ảnh không bị méo
                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.image_7bus),
                        contentDescription = "Bus Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = 10.dp) // Đẩy ảnh sang phải 1 chút cho đẹp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- TẦNG 3: SỐ CHỖ & GIÁ (FOOTER) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Chip "Còn ... chỗ" (MÀU TRẮNG NHƯ HÌNH)
                Surface(
                    color = AppWhite, // Nền trắng
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = AppDarkGray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Còn ${String.format("%02d", trip.seatsAvailable)} chỗ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppDarkGray // Chữ màu xám đen
                        )
                    }
                }

                // 2. Chip "Giá" (MÀU XANH NHẠT NHƯ HÌNH)
                Surface(
                    color = Color(0xFFA6CDC9), // Màu xanh ngọc nhạt
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        Text(
                            text = trip.price,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppWhite
                        )
                    }
                }
            }
        }
    }
}