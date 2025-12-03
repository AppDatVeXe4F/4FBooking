package com.example.a4f.screens.booking

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.a4f.navigation.BottomNavItem
import kotlinx.coroutines.delay
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.Locale

@Composable
fun QRCodeScreen(
    navController: NavController,
    totalPrice: Int,
    tripId: String
) {
    val context = LocalContext.current
    val formattedPrice = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(totalPrice)

    // --- 1. LOGIC ĐẾM NGƯỢC  ---
    var timeLeft by remember { mutableIntStateOf(600) }
    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    // CẤU HÌNH TÀI KHOẢN
    val BANK_ID = "MB"
    val ACCOUNT_NO = "0368003344"
    val ACCOUNT_NAME = "NHAT_THUY"
    val TEMPLATE = "compact2"
    val rawDescription = "VE_4F_$tripId"

    val encodedName = URLEncoder.encode(ACCOUNT_NAME, "UTF-8").replace("+", "%20")
    val encodedDesc = URLEncoder.encode(rawDescription, "UTF-8").replace("+", "%20")
    val qrUrl = "https://img.vietqr.io/image/$BANK_ID-$ACCOUNT_NO-$TEMPLATE.png?amount=$totalPrice&addInfo=$encodedDesc&accountName=$encodedName"

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // HEADER
        Column(modifier = Modifier.fillMaxWidth().background(AppGreen)) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                IconButton(onClick = {
                    navController.navigate(BottomNavItem.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        launchSingleTop = true
                    }
                }, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Text("THANH TOÁN", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.align(Alignment.Center))
            }
        }

        // BODY & FOOTER
        Box(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Tổng thanh toán", fontSize = 18.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = formattedPrice, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = AppGreen)

                Spacer(modifier = Modifier.height(24.dp))

                // QR Code
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp), modifier = Modifier.size(280.dp)) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        AsyncImage(model = qrUrl, contentDescription = "QR Code", modifier = Modifier.fillMaxSize().padding(16.dp), contentScale = ContentScale.Fit)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- 2. HIỂN THỊ THỜI GIAN ĐẾM NGƯỢC  ---
                Text(
                    text = "Thời gian quét mã còn $timeString",
                    color = Color(0xFF4DB6AC), // Màu xanh ngọc giống hình
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { Toast.makeText(context, "Đã lưu ảnh QR", Toast.LENGTH_SHORT).show() },
                    shape = RoundedCornerShape(50.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray),
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("Tải mã QR", color = AppGreen, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Hướng dẫn
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                    InstructionRow("1", "Tải mã QR về máy hoặc chụp màn hình mã QR bên trên")
                    InstructionRow("2", "Mở ứng dụng ngân hàng và chọn mục Thanh toán (Quét QR)")
                    InstructionRow("3", "Tiếp tục chọn quét QR tại mục thanh toán QR")
                    InstructionRow("4", "Nhấn vào Tải ảnh QR và chọn mã QR thanh toán đã lưu về máy")
                }

                Spacer(modifier = Modifier.height(100.dp))
            }

            // --- 3. NÚT GHIM CỨNG Ở ĐÁY ---
            Button(
                onClick = {
                    navController.navigate(BottomNavItem.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("ĐÃ THANH TOÁN XONG", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InstructionRow(number: String, text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(0xFFE0E0E0)), contentAlignment = Alignment.Center) {
            Text(text = number, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontSize = 14.sp, color = Color.Gray, lineHeight = 20.sp, modifier = Modifier.weight(1f))
    }
}