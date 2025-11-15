// trong com/example/a4f/screens/booking/FindTripScreen.kt


package com.example.a4f.screens.booking


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.a4f.R
import com.example.a4f.screens.BookingRoutes
import java.util.Random // <-- THÊM IMPORT NÀY


// --- DATA CLASS (Giữ nguyên) ---
data class TripData(
    val startTime: String,
    val endTime: String,
    val startLocation: String,
    val endLocation: String,
    val details: String,
    val seatsAvailable: String,
    val price: String,
    val imageResId: Int,
    val seatType: String
)


// --- DATA GIẢ (Giữ nguyên) ---
val sampleTrips = listOf(
    TripData("06:15", "10:15", "Bến xe Miền Tây", "VP Tân Châu", "Khoảng cách 185km - 4h", "03", "190000", R.drawable.image_7bus, "Limousine"),
    TripData("09:15", "13:15", "VP Long Xuyên", "Bến xe Miền Đông", "Khoảng cách 185km - 4h", "03", "200000", R.drawable.image_7bus, "Giường"),
    TripData("13:15", "17:15", "BX Miền Đông Mới", "VP Long Xuyên", "Khoảng cách 185km - 4h", "13", "200000", R.drawable.image_7bus, "Ghế"),
    TripData("13:15", "17:15", "Bến xe Miền Tây", "BX Miền Đông Mới", "Khoảng cách 185km - 4h", "01", "200000", R.drawable.image_7bus, "Limousine"),
    TripData("18:30", "22:30", "VP Long Xuyên", "Bến xe Miền Tây", "Khoảng cách 185km - 4h", "00", "200000", R.drawable.image_7bus, "Giường")
)
// ------------------------------


@Composable
fun FindTripScreen(navController: NavHostController) {
// --- Các màu sắc (Giữ nguyên) ---
    val darkTeal = Color(0xFF425E5E)
    val lightGrayBg = Color(0xFFF5F5F5)
    val chipGrayBg = Color(0xFFEAEAEA)
    val grayText = Color.Gray


    // --- Các biến state (Giữ nguyên) ---
    var selectedDate by remember { mutableStateOf("28/09") }
    var expandedFilter by remember { mutableStateOf<String?>(null) }
    val filterOptions = mapOf(
        "Giá" to listOf("Tăng dần", "Giảm dần"),
        "Loại ghế" to listOf("Ghế", "Giường", "Limousine"),
        "Giờ" to listOf("Sáng (06:00 - 12:00)", "Chiều (12:00 - 18:00)", "Tối (18:00 - 24:00)")
    )
    val selectedFilters = remember { mutableStateMapOf<String, String>() }


    // ---  1: LOGIC LỌC (ĐÃ CẬP NHẬT) ---
    // Cập nhật lại danh sách này mỗi khi BỘ LỌC hoặc NGÀY thay đổi
    val filteredList = remember(selectedFilters.toMap(), selectedDate) {


        // --- BƯỚC A: TẠO DATA GIẢ TƯƠNG ỨNG VỚI NGÀY ---
        // (Để mô phỏng việc danh sách thay đổi theo ngày)
        val tripsForDate = when (selectedDate) {
            "29/09" -> sampleTrips.shuffled(Random(1)) // Xáo trộn danh sách cho "Th 2"
            "30/09" -> sampleTrips.take(3) // Chỉ lấy 3 chuyến cho "Th 3"
            "01/10" -> sampleTrips.filterNot { it.seatType == "Limousine" } // "Th 4" không có Limo
            "02/10" -> sampleTrips.shuffled(Random(2)) // Xáo trộn kiểu khác cho "Th 5"
            else -> sampleTrips // Các ngày còn lại dùng danh sách gốc
        }


        // --- BƯỚC B: LỌC DANH SÁCH (ĐÃ TẠO Ở TRÊN) ---
        // (Logic lọc giữ nguyên)
        tripsForDate.filter { trip ->
            // Kiểm tra "Loại ghế"
            val seatFilter = selectedFilters["Loại ghế"]
            val seatMatch = if (seatFilter != null) trip.seatType == seatFilter else true


            // Kiểm tra "Giờ"
            val timeFilter = selectedFilters["Giờ"]
            val timeMatch = if (timeFilter != null) {
                val hour = trip.startTime.substringBefore(':').toInt()
                when (timeFilter) {
                    "Sáng (06:00 - 12:00)" -> hour in 6..11
                    "Chiều (12:00 - 18:00)" -> hour in 12..17
                    "Tối (18:00 - 24:00)" -> hour in 18..23
                    else -> true
                }
            } else true


            seatMatch && timeMatch
        }
    }


    // --- SỬA 2: SẮP XẾP (Giữ nguyên) ---
    // Sắp xếp danh sách (đã lọc) dựa trên bộ lọc "Giá"
    val sortedAndFilteredList = remember(filteredList, selectedFilters["Giá"]) {
        val priceFilter = selectedFilters["Giá"]
        when (priceFilter) {
            "Tăng dần" -> filteredList.sortedBy { it.price.toLong() }
            "Giảm dần" -> filteredList.sortedByDescending { it.price.toLong() }
            else -> filteredList
        }
    }
    // ----------------------------------------------------


    Column(modifier = Modifier
        .fillMaxSize()
        .background(lightGrayBg)) {
// --- PHẦN 1: THANH CHỌN NGÀY (Giữ nguyên) ---
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val days = listOf(
                Pair("CN", "28/09"), Pair("Th 2", "29/09"), Pair("Th 3", "30/09"),
                Pair("Th 4", "01/10"), Pair("Th 5", "02/10"), Pair("Th 6", "03/10"),
                Pair("Th 7", "04/10")
            )
            items(days) { dayPair ->
                DateChip(
                    day = dayPair.first,
                    date = dayPair.second,
                    isSelected = (dayPair.second == selectedDate),
                    selectedColor = darkTeal,
                    unselectedColor = chipGrayBg,
                    onClick = {
                        selectedDate = dayPair.second // Cập nhật ngày
                        // TODO: Bạn có thể reset bộ lọc tại đây nếu muốn
                        // selectedFilters.clear()
                    }
                )
            }
        }


        // --- PHẦN 2: THANH LỌC (Giữ nguyên) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Top
        ) {
            filterOptions.forEach { (filterName, options) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box {
                        FilterButton(
                            text = filterName,
                            backgroundColor = chipGrayBg,
                            textColor = grayText,
                            isSelected = (expandedFilter == filterName),
                            activeColor = darkTeal,
                            onClick = { expandedFilter = filterName }
                        )
                        DropdownMenu(
                            expanded = (expandedFilter == filterName),
                            onDismissRequest = { expandedFilter = null }
                        ) {
                            options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        if (selectedFilters[filterName] == option) {
                                            selectedFilters.remove(filterName)
                                        } else {
                                            selectedFilters[filterName] = option
                                        }
                                        expandedFilter = null
                                    }
                                )
                            }
                        }
                    }
                    val currentSelection = selectedFilters[filterName]
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentSelection ?: "",
                        color = darkTeal,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }


        // --- PHẦN 3: LAZYCOLUMN (Giữ nguyên) ---
        // Nó sẽ tự động cập nhật vì `sortedAndFilteredList` đã thay đổi
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sortedAndFilteredList) { trip ->
                TripCard(
                    navController = navController,
                    trip = trip,
                    cardColor = darkTeal
                )
            }
        }
    }
}




// --- CÁC COMPOSABLE PHỤ (Tất cả giữ nguyên) ---


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripCard(
    navController: NavHostController,
    trip: TripData,
    cardColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { navController.navigate(BookingRoutes.SELECT_SEAT) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = trip.startTime,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = trip.endTime,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row {
                        RouteTimeline()
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.height(70.dp)
                        ) {
                            Text(trip.startLocation, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text(trip.details, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            Text(trip.endLocation, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    AvailableSeatsChip(seats = trip.seatsAvailable)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = trip.imageResId),
                        contentDescription = "Hình nhà xe",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(width = 110.dp, height = 80.dp)
                    )
                    PriceChip(price = trip.price.toLong())
                }
            }
        }
    }
}


@Composable
fun RouteTimeline() {
    val dotColor = Color.White
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    Canvas(modifier = Modifier
        .height(70.dp)
        .width(10.dp)) {
        drawCircle(color = dotColor, radius = 6f, center = center.copy(y = 0f))
        drawLine(
            color = dotColor,
            start = center.copy(y = 6f),
            end = center.copy(y = size.height - 6f),
            strokeWidth = 3f,
            pathEffect = pathEffect
        )
        drawCircle(color = dotColor, radius = 6f, center = center.copy(y = size.height))
    }
}


@Composable
fun AvailableSeatsChip(seats: String) {
    val annotatedString = buildAnnotatedString {
        append("Còn ")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(seats) }
        append(" chỗ")
    }
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White,
        contentColor = Color.Black
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Person, "Chỗ", modifier = Modifier.size(16.dp), tint = Color(0xFF425E5E))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = annotatedString, fontSize = 12.sp)
        }
    }
}


@Composable
fun PriceChip(price: Long) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White,
        contentColor = Color(0xFF425E5E)
    ) {
        Text(
            text = "%,dđ".format(price).replace(',', '.'),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateChip(
    day: String,
    date: String,
    isSelected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    onClick: () -> Unit
) {
    val cardColor = if (isSelected) selectedColor else unselectedColor
    val textColor = if (isSelected) Color.White else Color.Gray


    Card(
        modifier = Modifier.width(65.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(day, style = MaterialTheme.typography.labelMedium, color = textColor, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(date, style = MaterialTheme.typography.bodyMedium, color = textColor)
        }
    }
}


@Composable
fun FilterButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val contentColor = if (isSelected) activeColor else textColor


    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
    ) {
        Text(text)
        Icon(Icons.Default.ArrowDropDown, contentDescription = "Lọc", tint = contentColor)
    }
}





