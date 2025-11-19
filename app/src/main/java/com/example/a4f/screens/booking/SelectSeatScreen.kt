// File: com/example/a4f/screens/booking/SelectSeatScreen.kt
package com.example.a4f.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.a4f.navigation.AppRoutes

// ================== TRẠNG THÁI GHẾ ==================
enum class SeatState { Available, Sold, Selecting }

// ================== MÀU SẮC ==================
val redColor = Color(0xFFE53935)
val greenishBlue = Color(0xFF32CCBA)
val grayColor = Color(0xFF949494)
val buttonColor = Color(0xFF49736E)

val availableBgColor = Color.White
val availableBorderColor = grayColor
val availableContentColor = grayColor
val soldBgColor = redColor
val soldBorderColor = Color.White
val soldContentColor = Color.White
val selectingBgColor = greenishBlue
val selectingBorderColor = Color.White
val selectingContentColor = Color.White

// ================== DỮ LIỆU GHẾ GIẢ ==================
val initialSeatStates = mapOf(
    "A01" to SeatState.Sold, "A02" to SeatState.Sold, "A03" to SeatState.Sold,
    "A04" to SeatState.Sold, "A05" to SeatState.Sold, "A06" to SeatState.Sold,
    "A07" to SeatState.Sold, "A08" to SeatState.Sold, "A09" to SeatState.Sold,
    "A10" to SeatState.Sold, "A11" to SeatState.Available, "A12" to SeatState.Available,
    "A13" to SeatState.Available, "A14" to SeatState.Available,
    "B01" to SeatState.Sold, "B02" to SeatState.Sold, "B03" to SeatState.Sold,
    "B04" to SeatState.Sold, "B05" to SeatState.Sold, "B06" to SeatState.Sold,
    "B07" to SeatState.Sold, "B08" to SeatState.Sold, "B09" to SeatState.Available,
    "B10" to SeatState.Available, "B11" to SeatState.Available, "B12" to SeatState.Sold,
    "B13" to SeatState.Available, "B14" to SeatState.Available
)

private data class SeatStyle(
    val bgColor: Color,
    val borderColor: Color,
    val contentColor: Color,
    val elevation: Dp
)

// ================== MÀN HÌNH CHỌN GHẾ CHÍNH ==================
@Composable
fun SelectSeatScreen(navController: NavHostController) {
    val seatStates = remember { mutableStateOf(initialSeatStates) }

    val pricePerTicket = 200000L
    val selectedSeatsList = seatStates.value.filter { it.value == SeatState.Selecting }.keys.toList()
    val ticketCount = selectedSeatsList.size
    val seatLabels = selectedSeatsList.joinToString(", ")
    val totalPrice = ticketCount * pricePerTicket
    val formattedPrice = "%,dđ".format(totalPrice).replace(',', '.')

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Nút Back
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Text("Chọn ghế", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        SeatLegend(modifier = Modifier.padding(horizontal = 16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { FloorHeader("Tầng 1") }
            item { FloorSeats(seatStates, 'A') }
            item { FloorHeader("Tầng 2") }
            item { FloorSeats(seatStates, 'B') }
        }

        if (ticketCount > 0) {
            TicketSummaryCard(ticketCount, seatLabels, formattedPrice)
        }

        Button(
            onClick = { navController.navigate(AppRoutes.FILL_INFO) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            enabled = ticketCount > 0
        ) {
            Text("Tiếp tục", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

// ================== CÁC HÀM HỖ TRỢ ==================
@Composable
private fun FloorSeats(seatStates: MutableState<Map<String, SeatState>>, floor: Char) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Tạo tự động các hàng ghế theo tầng
        val rows = when (floor) {
            'A' -> listOf(
                listOf("A01", "A02"),
                listOf("A03", "A04", "A05"),
                listOf("A06", "A07", "A08"),
                listOf("A09", "A10", "A11"),
                listOf("A12", "A13", "A14")
            )
            else -> listOf(
                listOf("B01", "B02"),
                listOf("B03", "B04", "B05"),
                listOf("B06", "B07", "B08"),
                listOf("B09", "B10", "B11"),
                listOf("B12", "B13", "B14")
            )
        }

        rows.forEach { rowSeats ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowSeats.forEach { seat ->
                    if (seat.isNotEmpty()) {
                        SeatIcon(seatLabel = seat, seatStates = seatStates)
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.SeatIcon(seatLabel: String, seatStates: MutableState<Map<String, SeatState>>) {
    val state = seatStates.value[seatLabel] ?: SeatState.Available
    val style = when (state) {
        SeatState.Available -> SeatStyle(availableBgColor, availableBorderColor, availableContentColor, 4.dp)
        SeatState.Sold -> SeatStyle(soldBgColor, soldBorderColor, soldContentColor, 0.dp)
        SeatState.Selecting -> SeatStyle(selectingBgColor, selectingBorderColor, selectingContentColor, 4.dp)
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(style.bgColor)
            .border(1.dp, style.borderColor, RoundedCornerShape(8.dp))
            .graphicsLayer {
                shadowElevation = style.elevation.toPx()
                shape = RoundedCornerShape(8.dp)
                ambientShadowColor = Color.Black.copy(alpha = 0.3f)
                spotShadowColor = Color.Black.copy(alpha = 0.3f)
            }
            .clickable(enabled = state != SeatState.Sold) {
                val newState = if (state == SeatState.Available) SeatState.Selecting else SeatState.Available
                if (newState != state) {
                    seatStates.value = seatStates.value.toMutableMap().apply { this[seatLabel] = newState }
                }
            }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CustomSeatShape(modifier = Modifier.size(24.dp), contentColor = style.contentColor)
            Spacer(Modifier.height(4.dp))
            Text(seatLabel, color = style.contentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CustomSeatShape(modifier: Modifier = Modifier, contentColor: Color) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.width(18.dp).height(8.dp).background(contentColor, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)))
        Spacer(Modifier.height(2.dp))
        Box(modifier = Modifier.width(22.dp).height(6.dp).background(contentColor, RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp)))
    }
}

@Composable
fun SeatLegend(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFF0F0F0)).padding(12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        LegendItem(availableBgColor, availableBorderColor, availableContentColor, "Còn trống")
        LegendItem(soldBgColor, soldBorderColor, soldContentColor, "Đã bán")
        LegendItem(selectingBgColor, selectingBorderColor, selectingContentColor, "Đang chọn")
    }
}

@Composable
fun LegendItem(bgColor: Color, borderColor: Color, contentColor: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(width = 30.dp, height = 24.dp).clip(RoundedCornerShape(6.dp)).background(bgColor).border(1.dp, borderColor, RoundedCornerShape(6.dp)).graphicsLayer { shadowElevation = 4.dp.toPx() }, contentAlignment = Alignment.Center) {
            CustomSeatShape(Modifier.size(18.dp), contentColor)
        }
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 14.sp)
    }
}

@Composable
fun FloorHeader(text: String) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFFE0E0E0)).padding(16.dp))
}

@Composable
fun TicketSummaryCard(ticketCount: Int, seatLabels: String, formattedPrice: String) {
    Surface(modifier = Modifier.fillMaxWidth().padding(16.dp), color = Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Chiều đi:", fontWeight = FontWeight.Bold)
            Text("$ticketCount vé")
            Text(seatLabels, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Số tiền:")
                Spacer(Modifier.width(8.dp))
                Text(formattedPrice, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
            }
        }
    }
}