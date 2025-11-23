package com.example.a4f.screens.booking

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.Locale
import com.example.a4f.navigation.BottomNavItem // Import NavItem

@Composable
fun QRCodeScreen(
    navController: NavController,
    totalPrice: Int,
    tripId: String
) {
    val context = LocalContext.current
    val formattedPrice = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(totalPrice)

    // CẤU HÌNH TÀI KHOẢN
    val BANK_ID = "MB"
    val ACCOUNT_NO = "0368003344"
    val ACCOUNT_NAME = "NHAT_THUY" // Không dấu cách
    val TEMPLATE = "compact2"

    val rawDescription = "VE_4F_$tripId" // Không dấu cách

    // Mã hóa URL
    val encodedName = URLEncoder.encode(ACCOUNT_NAME, "UTF-8").replace("+", "%20")
    val encodedDesc = URLEncoder.encode(rawDescription, "UTF-8").replace("+", "%20")

    val qrUrl = "https://img.vietqr.io/image/$BANK_ID-$ACCOUNT_NO-$TEMPLATE.png?amount=$totalPrice&addInfo=$encodedDesc&accountName=$encodedName"

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Header
        Column(modifier = Modifier.fillMaxWidth().background(AppGreen)) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Text("THANH TOÁN", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.align(Alignment.Center))
            }
        }

        // Body
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Tổng thanh toán", fontSize = 16.sp, color = Color.Gray)
            Text(text = "$formattedPrice", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = AppGreen)
            Spacer(modifier = Modifier.height(24.dp))

            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp), modifier = Modifier.size(300.dp)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AsyncImage(model = qrUrl, contentDescription = "QR Code", modifier = Modifier.size(260.dp), contentScale = ContentScale.Fit)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(onClick = { Toast.makeText(context, "Đã lưu ảnh QR", Toast.LENGTH_SHORT).show() }, shape = RoundedCornerShape(24.dp)) {
                Text("Tải mã QR", color = AppGreen)
            }
        }

        // Footer
        Button(
            onClick = {
                // Quay về trang chủ
                navController.navigate(BottomNavItem.Home.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                    launchSingleTop = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("ĐÃ THANH TOÁN XONG", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}