package com.example.a4f.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.a4f.data.FirestoreRepository
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

val PaymentBoxBg = Color(0xFFCFE0DE)

@Composable
fun PaymentScreen(
    navController: NavController,
    source: String?,
    destination: String?,
    date: String?,
    totalPrice: Int,
    tripId: String,
    selectedSeats: String,
    userName: String,
    userPhone: String,
    userEmail: String
) {
    val displayDate = date?.replace("-", "/") ?: ""
    val formattedPrice = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(totalPrice)
    var timeLeft by remember { mutableIntStateOf(600) } // 600 giây = 10 phút

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // 1. HEADER
        Column(modifier = Modifier.fillMaxWidth().background(AppGreen)) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${source?.uppercase()} → ${destination?.uppercase()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = displayDate, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }

        // 2. BODY
        Column(modifier = Modifier.weight(1f).padding(16.dp)) {
            BookingStepperPayment()
            Spacer(modifier = Modifier.height(24.dp))

            // Thông tin khách hàng
            InfoTextRow("Họ và tên :", userName)
            InfoTextRow("Số điện thoại :", userPhone)
            InfoTextRow("Email :", userEmail)

            Spacer(modifier = Modifier.height(24.dp))

            // Thanh toán
            Card(
                colors = CardDefaults.cardColors(containerColor = PaymentBoxBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PaymentRow("Giá vé", "${formattedPrice}đ")
                    PaymentRow("Phí thanh toán", "0đ")

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tổng tiền
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text("Thanh toán mặc định với", color = AppGreen, fontSize = 14.sp)
                        Text("${formattedPrice}đ", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color.Black)
                    }
                }
            }
        }

        // 3. FOOTER
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Thời gian giữ vé còn lại $timeString",
                color = AppGreen,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    // --- THANH TOÁN THÀNH CÔNG ---
                    // 1. Cập nhật ghế đã bán lên Firebase
                    val seatsList = selectedSeats.split(",").map { it.trim() }
                    FirestoreRepository.bookSeats(tripId, seatsList)

                    // 2. Quay về trang chủ
                    navController.navigate("home") {
                        popUpTo(0) // Xóa sạch lịch sử
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("THANH TOÁN", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- COMPONENTS CON ---

@Composable
fun InfoTextRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 16.sp)
        Text(value, color = Color.Black, fontSize = 16.sp)
    }
}

@Composable
fun PaymentRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = AppGreen, fontSize = 15.sp)
        Text(value, color = Color.Gray, fontSize = 15.sp)
    }
}

@Composable
fun BookingStepperPayment() {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Thời gian", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Text("Chọn ghế", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Text("Thông tin", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))

        // Bước hiện tại: THANH TOÁN
        Surface(color = AppGreen, shape = RoundedCornerShape(20.dp), modifier = Modifier.height(28.dp)) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                Text("THANH TOÁN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AppLightGreen))
        Divider(modifier = Modifier.weight(1f).height(1.dp), color = AppGreen)
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AppLightGreen))
        Divider(modifier = Modifier.weight(1f).height(1.dp), color = AppGreen)
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AppLightGreen))
        Divider(modifier = Modifier.weight(1f).height(1.dp), color = AppGreen)
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(AppGreen))
    }
}