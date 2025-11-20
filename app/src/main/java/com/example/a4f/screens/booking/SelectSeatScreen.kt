package com.example.a4f.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.NumberFormat
import java.util.Locale

// Màu sắc
val SeatAvailableColor = Color.White
val SeatSoldColor = Color(0xFFFF4D4D)
val SeatSelectedColor = Color(0xFF4DB6AC)
val BorderGray = Color(0xFFBDBDBD)

// Data ghế
data class Seat(val id: String, val floor: Int, val isSold: Boolean = false)

@Composable
fun SelectSeatScreen(
    navController: NavController,
    tripId: String = "1", // NHẬN ID CHUYẾN XE
    pricePerTicket: Int = 200000,
    source: String? = "TP. HỒ CHÍ MINH",
    destination: String? = "AN GIANG",
    date: String? = "Chủ nhật, 28-09-2025"
) {
    val selectedSeats = remember { mutableStateListOf<String>() }

    // --- LOGIC QUAN TRỌNG: TẠO DANH SÁCH GHẾ ĐÃ BÁN KHÁC NHAU DỰA VÀO TRIP ID ---
    val soldSeatsList = remember(tripId) { getSoldSeatsByTripId(tripId) }

    // Tạo ghế Tầng 1 và Tầng 2 dựa trên danh sách đã bán ở trên
    val seatsFloor1 = remember(tripId) { generateSeats(1, soldSeatsList) }
    val seatsFloor2 = remember(tripId) { generateSeats(2, soldSeatsList) }

    val totalPrice = selectedSeats.size * pricePerTicket

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // HEADER
        Column(modifier = Modifier.fillMaxWidth().background(AppGreen)) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${source?.uppercase()} → ${destination?.uppercase()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    val displayDate = date?.replace("-", "/") ?: ""
                    Text(text = displayDate, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            BookingStepperForSeat()
            Spacer(modifier = Modifier.height(16.dp))
            SeatLegend()
        }

        // DANH SÁCH GHẾ (Padding bottom 200dp như bạn yêu cầu lúc nãy)
        LazyColumn(
            modifier = Modifier.weight(1f).background(Color.White),
            contentPadding = PaddingValues(bottom = 200.dp)
        ) {
            item { FloorHeader("Tầng 1") }
            item { SeatGridSection(seatsFloor1, selectedSeats) }
            item { FloorHeader("Tầng 2") }
            item { SeatGridSection(seatsFloor2, selectedSeats) }
        }
    }

    // FOOTER
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        SeatSummaryFooter(selectedSeats, totalPrice, onContinue = { })
    }
}

// --- HÀM QUAN TRỌNG: QUY ĐỊNH GHẾ ĐÃ BÁN CHO TỪNG CHUYẾN ---
fun getSoldSeatsByTripId(tripId: String): List<String> {
    return when (tripId) {
        "1" -> listOf("A01", "A02", "B05", "B06") // Chuyến 1: Bán 4 ghế này
        "2" -> listOf("A03", "A04", "A05", "B01", "B02", "B10") // Chuyến 2: Bán 6 ghế khác
        "3" -> listOf("A01", "A10", "A11", "B13", "B14") // Chuyến 3: Bán ghế cuối
        "4" -> listOf("A05", "A06", "B05", "B06") // Chuyến 4: Bán ghế giữa
        "5" -> listOf() // Chuyến 5: Còn trống hết
        "6" -> listOf("A01", "A02", "A03", "A04", "A05", "A06", "A07", "A08") // Chuyến 6: Tầng 1 gần hết
        else -> listOf("A01", "B01") // Mặc định
    }
}

// --- CÁC COMPONENT CON (GIỮ NGUYÊN GIAO DIỆN ĐẸP CỦA BẠN) ---

@Composable
fun SeatGridSection(seats: List<Seat>, selectedSeats: MutableList<String>) {
    val rows = seats.chunked(3)
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)) {
        rows.forEach { rowSeats ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                if (rowSeats.isNotEmpty()) SeatItem(rowSeats[0], selectedSeats) else Spacer(Modifier.size(48.dp))
                if (rowSeats.size > 1) SeatItem(rowSeats[1], selectedSeats) else Spacer(Modifier.size(48.dp))
                if (rowSeats.size > 2) SeatItem(rowSeats[2], selectedSeats) else Spacer(Modifier.size(48.dp))
            }
        }
    }
}

@Composable
fun SeatItem(seat: Seat, selectedSeats: MutableList<String>) {
    val isSelected = selectedSeats.contains(seat.id)
    val bgColor = when {
        seat.isSold -> SeatSoldColor
        isSelected -> SeatSelectedColor
        else -> SeatAvailableColor
    }
    val borderColor = if (seat.isSold || isSelected) Color.Transparent else BorderGray
    val textColor = if (seat.isSold || isSelected) Color.White else Color.Black

    Box(
        modifier = Modifier.size(width = 60.dp, height = 50.dp).clip(RoundedCornerShape(8.dp)).background(bgColor).border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = !seat.isSold) {
                if (isSelected) selectedSeats.remove(seat.id) else selectedSeats.add(seat.id)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.EventSeat, contentDescription = null, tint = textColor.copy(alpha = 0.8f), modifier = Modifier.size(24.dp))
            Text(text = seat.id, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}

@Composable
fun FloorHeader(title: String) {
    Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFE0E0E0)).padding(vertical = 8.dp, horizontal = 16.dp)) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun SeatLegend() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        LegendItem(color = SeatAvailableColor, text = "Còn trống", hasBorder = true)
        Spacer(modifier = Modifier.width(16.dp))
        LegendItem(color = SeatSoldColor, text = "Đã bán")
        Spacer(modifier = Modifier.width(16.dp))
        LegendItem(color = SeatSelectedColor, text = "Đang chọn")
    }
}

@Composable
fun LegendItem(color: Color, text: String, hasBorder: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(20.dp).clip(RoundedCornerShape(4.dp)).background(color).then(if (hasBorder) Modifier.border(1.dp, BorderGray, RoundedCornerShape(4.dp)) else Modifier))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp, color = Color.Black)
    }
}

@Composable
fun SeatSummaryFooter(selectedSeats: List<String>, totalPrice: Int, onContinue: () -> Unit) {
    Card(shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), elevation = CardDefaults.cardElevation(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "${selectedSeats.size} vé", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = if (selectedSeats.isEmpty()) "Chưa chọn ghế" else selectedSeats.joinToString(", "), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                val formattedPrice = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(totalPrice)
                Text(text = "${formattedPrice}đ", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onContinue, enabled = selectedSeats.isNotEmpty(), colors = ButtonDefaults.buttonColors(containerColor = AppGreen), modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp)) {
                Text("Tiếp tục", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
fun BookingStepperForSeat() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Thời gian", fontSize = 12.sp, color = Color.Gray)
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            Surface(color = AppGreen, shape = RoundedCornerShape(20.dp), modifier = Modifier.height(28.dp)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                    Text("CHỌN GHẾ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            Text("Thông tin", fontSize = 12.sp, color = Color.Gray)
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            Text("Thanh toán", fontSize = 12.sp, color = Color.Gray)
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AppLightGreen))
            Divider(modifier = Modifier.weight(1f).height(1.dp), color = AppGreen)
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(AppGreen))
            Divider(modifier = Modifier.weight(1f).height(1.dp), color = AppLightGreen)
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AppLightGreen))
            Divider(modifier = Modifier.weight(1f).height(1.dp), color = AppLightGreen)
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AppLightGreen))
        }
    }
}

fun generateSeats(floor: Int, soldList: List<String>): List<Seat> {
    val list = mutableListOf<Seat>()
    val prefix = if (floor == 1) "A" else "B"
    for (i in 1..14) {
        val id = String.format("%s%02d", prefix, i)
        list.add(Seat(id, floor, isSold = soldList.contains(id)))
    }
    return list
}