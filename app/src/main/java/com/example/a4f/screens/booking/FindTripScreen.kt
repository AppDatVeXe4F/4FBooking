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
import androidx.compose.ui.res.stringResource
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
    val seatType: String
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
    date: String?
) {
    // --- 1.  NGÀY THÁNG ---
    val dateListObj = remember(date) { generateNext7Days(date) }
    var selectedDateIndex by remember { mutableIntStateOf(0) }
    val currentTopBarDate = dateListObj.getOrNull(selectedDateIndex)?.fullDateString ?: stringResource(R.string.loading_trips)

    // --- 2. DỮ LIỆU (lấy từ Firestore) ---
    val tripsState = produceState(initialValue = emptyList<com.example.a4f.data.FirestoreRepository.TripModel>(), key1 = selectedDateIndex, key2 = source, key3 = destination) {
        val queryDate = dateListObj.getOrNull(selectedDateIndex)?.shortDate
        value = com.example.a4f.data.FirestoreRepository.getTrips(source, destination, queryDate)
    }

    val allTrips = tripsState.value.map { tm ->
        Trip(
            id = tm.id,
            startTime = tm.startTime,
            startStation = tm.startStation,
            endTime = tm.endTime,
            endStation = tm.endStation,
            distanceTime = tm.distanceTime,
            price = if (tm.price > 0) "${tm.price}đ" else "0đ",
            seatsAvailable = tm.seatsAvailable,
            seatType = tm.seatType
        )
    }

    // --- 3. FILTER ---
    val defaultSort = stringResource(R.string.default_sort)
    val allOption = stringResource(R.string.all)
    val bedSeatStr = stringResource(R.string.bed_seat)
    val limousineStr = stringResource(R.string.limousine)
    val morningStr = stringResource(R.string.morning_shift)
    val afternoonStr = stringResource(R.string.afternoon_shift)
    val eveningStr = stringResource(R.string.evening_shift)
    val ascendingStr = stringResource(R.string.ascending)
    val descendingStr = stringResource(R.string.descending)
    
    var sortOption by remember { mutableStateOf(defaultSort) }
    var seatFilter by remember { mutableStateOf(allOption) }
    var timeFilter by remember { mutableStateOf(allOption) }

    val filteredTrips = remember(allTrips, sortOption, seatFilter, timeFilter, allOption, bedSeatStr, limousineStr, morningStr, afternoonStr, eveningStr, ascendingStr, descendingStr) {
        var result = allTrips

        if (seatFilter != allOption) {
            result = result.filter { 
                (seatFilter == bedSeatStr && it.seatType == "Giường nằm") || 
                (seatFilter == limousineStr && it.seatType == "Limousine")
            }
        }
        if (timeFilter != allOption) {
            result = result.filter { trip ->
                val h = trip.startHour
                when (timeFilter) {
                    morningStr -> h in 6..11
                    afternoonStr -> h in 12..17
                    eveningStr -> h in 18..22
                    else -> true
                }
            }
        }
        when (sortOption) {
            ascendingStr -> result.sortedBy { it.realPrice }
            descendingStr -> result.sortedByDescending { it.realPrice }
            else -> result
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(AppGreen)) {
        // --- HEADER ---
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back), tint = AppWhite)
                }
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${source?.uppercase() ?: stringResource(R.string.default_departure)} → ${destination?.uppercase() ?: stringResource(R.string.default_arrival)}",
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
            // DANH SÁCH NGÀY
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
                        Text(stringResource(R.string.no_trips_found), modifier = Modifier.fillMaxWidth().padding(20.dp), textAlign = TextAlign.Center, color = Color.Gray)
                    }
                } else {
                    items(filteredTrips) { trip ->
                        TripCardItem(trip = trip, onClick = {
                            val priceInt = trip.realPrice
                            val src = source ?: "TP. HCM"
                            val dest = destination ?: "AN GIANG"
                            val dateStr = currentTopBarDate.replace("/", "-")
                            val tripId = trip.id
                            val time = trip.startTime
                            navController.navigate("select_seat_screen/$tripId/$priceInt/$src/$dest/$dateStr/$time")
                        })
                    }
                }
            }
        }
    }
}

// --- DỮ LIỆU (6 Chuyến/Ngày) ---
// --- HÀM TẠO DỮ LIỆU GIẢ LẬP  ---
fun generateMockTripsForDate(index: Int, source: String?, dest: String?): List<Trip> {
    // 1. Lấy điểm đi/đến:
    val startPoint = source ?: "Bến xe Miền Tây"
    val endPoint = dest ?: "VP Long Xuyên"

    val offset = index * 15
    val totalSeats = 28

    return listOf(
        Trip(
            id = "trip_001",
            startTime = "${6 + (index % 2)}:${15 + (index * 5) % 45}",
            startStation = startPoint,
            endTime = "${10 + (index % 2)}:${15 + (index * 5) % 45}",
            endStation = endPoint,
            distanceTime = "185km - 4h",
            price = "230.000đ",
            seatsAvailable = totalSeats - getSoldSeatsCount("trip_001"),
            seatType = "Limousine"
        ),
        Trip(
            id = "trip_002",
            startTime = "${8 + (index % 2)}:00",
            startStation = startPoint,
            endTime = "${12 + (index % 2)}:00",
            endStation = endPoint,
            distanceTime = "185km - 4h",
            price = "230.000đ",
            seatsAvailable = totalSeats - getSoldSeatsCount("trip_002"),
            seatType = "Limousine"
        ),
        Trip(
            id = "trip_003",
            startTime = "${10 + (index % 2)}:30",
            startStation = startPoint,
            endTime = "${14 + (index % 2)}:30",
            endStation = endPoint,
            distanceTime = "185km - 4h",
            price = "200.000đ",
            seatsAvailable = totalSeats - getSoldSeatsCount("trip_003"),
            seatType = "Giường nằm"
        ),
        Trip(
            id = "trip_004",
            startTime = "13:${15 + offset % 45}",
            startStation = startPoint,
            endTime = "17:${15 + offset % 45}",
            endStation = endPoint,
            distanceTime = "185km - 4h",
            price = "200.000đ", // Đã sửa lỗi 2 triệu -> 200k
            seatsAvailable = totalSeats - getSoldSeatsCount("trip_004"),
            seatType = "Giường nằm"
        ),
        Trip(
            id = "trip_005",
            startTime = "15:00",
            startStation = startPoint,
            endTime = "19:00",
            endStation = endPoint,
            distanceTime = "185km - 4h",
            price = "230.000đ",
            seatsAvailable = totalSeats - getSoldSeatsCount("trip_005"),
            seatType = "Limousine"
        ),
        Trip(
            id = "trip_006",
            startTime = "22:00",
            startStation = startPoint,
            endTime = "02:00",
            endStation = endPoint,
            distanceTime = "185km - 4h",
            price = "200.000đ",
            seatsAvailable = totalSeats - getSoldSeatsCount("trip_006"),
            seatType = "Giường nằm"
        )
    )
}

// --- HÀM ĐẾM SỐ GHẾ GIẢ LẬP  ---
fun getSoldSeatsCount(tripId: String): Int {
    return when (tripId) {
        "trip_001" -> 4
        "trip_002" -> 6
        "trip_003" -> 5
        "trip_004" -> 4
        "trip_005" -> 0
        "trip_006" -> 8
        else -> 2
    }
}

// --- HÀM NGÀY THÁNG ---
data class DateUIModel(
    val dayOfWeek: String,
    val shortDate: String,
    val fullDateString: String
)

fun generateNext7Days(startDateString: String?): List<DateUIModel> {
    val list = mutableListOf<DateUIModel>()
    val inputFormat = SimpleDateFormat("dd,'Th'M yyyy", Locale("vi", "VN"))
    val cal = Calendar.getInstance()

    try {
        if (!startDateString.isNullOrBlank()) {
            cal.time = inputFormat.parse(startDateString) ?: Date()
        }
    } catch (e: Exception) {
        cal.time = Date()
    }

    val dayOfWeekFormat = SimpleDateFormat("EEE", Locale("vi", "VN"))
    val shortDateFormat = SimpleDateFormat("dd/MM", Locale("vi", "VN")) //
    val fullDateFormat = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale("vi", "VN")) // TopBar

    for (i in 0 until 7) {
        var dayName = dayOfWeekFormat.format(cal.time).replace("Th ", "Th ")
        if (dayName.contains("CN") || dayName.contains("Chủ Nhật")) dayName = "CN"
        if (dayName.startsWith("t")) dayName = dayName.replace("t", "Th ")
        if (dayName.startsWith("T")) dayName = dayName.replace("T", "Th ").replace("Th h", "Th ")

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
        cal.add(Calendar.DAY_OF_MONTH, 1)
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
                    Text(text = stringResource(R.string.time_step), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppWhite)
                }
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = AppDarkGray, modifier = Modifier.size(20.dp))
            Text(stringResource(R.string.select_seat_step), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(R.string.information), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(R.string.payment), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
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
    val defaultSort = stringResource(R.string.default_sort)
    val allStr = stringResource(R.string.all)
    val priceLabel = stringResource(R.string.price_filter)
    val seatTypeLabel = stringResource(R.string.seat_type_filter)
    val timeLabel = stringResource(R.string.time_filter)
    val selectedLabel = stringResource(R.string.selected)
    val ascendingStr = stringResource(R.string.ascending)
    val descendingStr = stringResource(R.string.descending)
    val bedSeatStr = stringResource(R.string.bed_seat)
    val limousineStr = stringResource(R.string.limousine)
    val morningStr = stringResource(R.string.morning_shift)
    val afternoonStr = stringResource(R.string.afternoon_shift)
    val eveningStr = stringResource(R.string.evening_shift)
    
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FilterChipWithMenu(
            if (currentSort == defaultSort) priceLabel else "$priceLabel...", 
            listOf(defaultSort, ascendingStr, descendingStr), 
            expandedType == "PRICE", 
            currentSort != defaultSort, 
            { expandedType = if (it) "PRICE" else null }, 
            { onSortSelected(it); expandedType = null }
        )
        FilterChipWithMenu(
            if (currentSeat == allStr) seatTypeLabel else currentSeat, 
            listOf(allStr, bedSeatStr, limousineStr), 
            expandedType == "SEAT", 
            currentSeat != allStr, 
            { expandedType = if (it) "SEAT" else null }, 
            { onSeatSelected(it); expandedType = null }
        )
        FilterChipWithMenu(
            if (currentTime == allStr) timeLabel else selectedLabel, 
            listOf(allStr, morningStr, afternoonStr, eveningStr), 
            expandedType == "TIME", 
            currentTime != allStr, 
            { expandedType = if (it) "TIME" else null }, 
            { onTimeSelected(it); expandedType = null }
        )
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
            .height(220.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppGreen),
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
                    color = Color(0xFFFFE0E0)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- TẦNG 2: THÔNG TIN BẾN & ẢNH XE ---
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier.weight(1.1f)) {
                    // 1. Timeline
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .width(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color.Transparent)
                                .border(1.5.dp, AppDarkGray, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .width(1.5.dp)
                                .weight(1f)
                                .background(AppDarkGray)
                        )
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
                        Text(
                            text = trip.startStation,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppWhite
                        )
                        Text(
                            text = stringResource(R.string.distance, trip.distanceTime),
                            fontSize = 11.sp,
                            color = AppWhite.copy(alpha = 0.8f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            text = trip.endStation,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppWhite
                        )
                    }
                }

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
                            .offset(x = 10.dp)
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
                Surface(
                    color = AppWhite,
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
                            text = stringResource(R.string.seats_remaining, String.format("%02d", trip.seatsAvailable)),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppDarkGray
                        )
                    }
                }

                // 2. Chip "Giá"
                Surface(
                    color = Color(0xFFA6CDC9),
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