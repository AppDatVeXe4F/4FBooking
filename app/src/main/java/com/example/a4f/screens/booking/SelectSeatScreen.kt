// trong com/example/a4f/screens/booking/SelectSeatScreen.kt


package com.example.a4f.screens.booking


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.a4f.screens.BookingRoutes


// Định nghĩa 3 trạng thái của ghế
enum class SeatState {
    Available,
    Sold,
    Selecting
}


// --- Màu sắc  ---
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


// --- Data giả  ---
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


@Composable
fun SelectSeatScreen(navController: NavHostController) {
    val seatStates = remember { mutableStateOf(initialSeatStates) }


    val pricePerTicket = 200000L
    val selectedSeatsList = seatStates.value.filter { it.value == SeatState.Selecting }.keys.toList()
    val ticketCount = selectedSeatsList.size
    val seatLabels = selectedSeatsList.joinToString(", ")
    val totalPrice = ticketCount * pricePerTicket
    val formattedPrice = "%,dđ".format(totalPrice).replace(',', '.')


    Column(Modifier.fillMaxSize()) {
        SeatLegend(modifier = Modifier.padding(16.dp))


        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { FloorHeader("Tầng 1") }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // --- Các hàng ghế (Giữ nguyên) ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SeatIcon(seatLabel = "A01", seatStates = seatStates)
                        Spacer(Modifier.weight(1f))
                        SeatIcon(seatLabel = "A02", seatStates = seatStates)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SeatIcon(seatLabel = "A03", seatStates = seatStates)
                        SeatIcon(seatLabel = "A04", seatStates = seatStates)
                        SeatIcon(seatLabel = "A05", seatStates = seatStates)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SeatIcon(seatLabel = "A06", seatStates = seatStates)
                        SeatIcon(seatLabel = "A07", seatStates = seatStates)
                        SeatIcon(seatLabel = "A08", seatStates = seatStates)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SeatIcon(seatLabel = "A09", seatStates = seatStates)
                        SeatIcon(seatLabel = "A10", seatStates = seatStates)
                        SeatIcon(seatLabel = "A11", seatStates = seatStates)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SeatIcon(seatLabel = "A12", seatStates = seatStates)
                        SeatIcon(seatLabel = "A13", seatStates = seatStates)
                        SeatIcon(seatLabel = "A14", seatStates = seatStates)
                    }
                }
            }
            item { FloorHeader("Tầng 2") }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // --- Các hàng ghế (Giữ nguyên) ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SeatIcon(seatLabel = "B01", seatStates = seatStates)
                        Spacer(Modifier.weight(1f))
                        SeatIcon(seatLabel = "B02", seatStates = seatStates)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SeatIcon(seatLabel = "B03", seatStates = seatStates)
                        SeatIcon(seatLabel = "B04", seatStates = seatStates)
                        SeatIcon(seatLabel = "B05", seatStates = seatStates)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SeatIcon(seatLabel = "B06", seatStates = seatStates)
                        SeatIcon(seatLabel = "B07", seatStates = seatStates)
                        SeatIcon(seatLabel = "B08", seatStates = seatStates)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SeatIcon(seatLabel = "B09", seatStates = seatStates)
                        SeatIcon(seatLabel = "B10", seatStates = seatStates)
                        SeatIcon(seatLabel = "B11", seatStates = seatStates)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SeatIcon(seatLabel = "B12", seatStates = seatStates)
                        SeatIcon(seatLabel = "B13", seatStates = seatStates)
                        SeatIcon(seatLabel = "B14", seatStates = seatStates)
                    }
                }
            }
        } // Hết LazyColumn


        // --- Khung tóm tắt ---
        if (ticketCount > 0) {
            TicketSummaryCard(
                ticketCount = ticketCount,
                seatLabels = seatLabels,
                formattedPrice = formattedPrice
            )
        }


        // --- Nút "Tiếp tục"  ---
        Button(
            onClick = {
                navController.navigate(BookingRoutes.FILL_INFO)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            enabled = ticketCount > 0
        ) {
            Text("Tiếp tục", fontSize = 16.sp)
        }
    }
}


// --- Composable Khung tóm tắt  ---
@Composable
fun TicketSummaryCard(ticketCount: Int, seatLabels: String, formattedPrice: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color(0xFFF5F5F5),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Chiều đi:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$ticketCount vé",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = seatLabels,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Số tiền:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formattedPrice,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


// --- SỬA 1: COMPOSABLE GHẾ NGỒI ---
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
                val newState = if (state == SeatState.Available) {
                    SeatState.Selecting
                } else if (state == SeatState.Selecting) {
                    SeatState.Available
                } else {
                    state
                }
                if (newState != state) {
                    val newMap = seatStates.value.toMutableMap()
                    newMap[seatLabel] = newState
                    seatStates.value = newMap
                }
            }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {


            // --- SỬA 2: THAY THẾ ICON BẰNG HÌNH VẼ ---
            CustomSeatShape(
                modifier = Modifier.size(24.dp), // Kích thước
                contentColor = style.contentColor // Màu sắc
            )


            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = seatLabel,
                color = style.contentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// --- SỬA 3: COMPOSABLE MỚI ĐỂ VẼ HÌNH CÁI GHẾ ---
@Composable
fun CustomSeatShape(modifier: Modifier = Modifier, contentColor: Color) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Lưng ghế (Backrest)
        Box(
            modifier = Modifier
                .width(18.dp)
                .height(8.dp)
                .background(
                    color = contentColor,
                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                )
        )
        Spacer(modifier = Modifier.height(2.dp))
        // Đệm ngồi (Seat)
        Box(
            modifier = Modifier
                .width(22.dp)
                .height(6.dp)
                .background(
                    color = contentColor,
                    shape = RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp)
                )
        )
    }
}


// --- Composable chú thích (Giữ nguyên) ---
@Composable
fun SeatLegend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF0F0F0))
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(availableBgColor, availableBorderColor, availableContentColor, "Còn trống")
        LegendItem(soldBgColor, soldBorderColor, soldContentColor, "Đã bán")
        LegendItem(selectingBgColor, selectingBorderColor, selectingContentColor, "Đang chọn")
    }
}


// --- SỬA 4: COMPOSABLE ITEM CHÚ THÍCH (DÙNG HÌNH VẼ) ---
@Composable
fun LegendItem(bgColor: Color, borderColor: Color, contentColor: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(width = 30.dp, height = 24.dp) // Hộp chứa
                .clip(RoundedCornerShape(6.dp))
                .background(bgColor)
                .border(1.dp, borderColor, RoundedCornerShape(6.dp))
                .graphicsLayer {
                    shadowElevation = 4.dp.toPx()
                    shape = RoundedCornerShape(6.dp)
                    ambientShadowColor = Color.Black.copy(alpha = 0.3f)
                    spotShadowColor = Color.Black.copy(alpha = 0.3f)
                },
            contentAlignment = Alignment.Center
        ) {
            // Dùng lại hình vẽ cái ghế
            CustomSeatShape(
                modifier = Modifier.size(18.dp),
                contentColor = contentColor
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp)
    }
}


// --- Composable tiêu đề tầng (Giữ nguyên) ---
@Composable
fun FloorHeader(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(Color(0xFFE0E0E0))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}



