package com.example.a4f.screens.booking

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.a4f.R
import com.example.a4f.data.FirestoreRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
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
    val context = LocalContext.current // Để hiện thông báo lỗi nếu có
    val coroutineScope = rememberCoroutineScope()
    val displayDate = date?.replace("-", "/") ?: ""
    val formattedPrice = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(totalPrice)

    var timeLeft by remember { mutableIntStateOf(600) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            containerColor = Color.White,
            title = { Text(stringResource(R.string.notification_title), fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = { Text(stringResource(R.string.confirm_cancel_action), fontSize = 16.sp) },
            dismissButton = { TextButton(onClick = { showCancelDialog = false }) { Text(stringResource(R.string.cancel), color = Color.Gray) } },
            confirmButton = {
                TextButton(onClick = { showCancelDialog = false; navController.popBackStack() }) {
                    Text(stringResource(R.string.agree), color = AppGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // HEADER
        Column(modifier = Modifier.fillMaxWidth().background(AppGreen)) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                IconButton(onClick = { showCancelDialog = true }, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${source?.uppercase()} → ${destination?.uppercase()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = displayDate, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }

        // BODY
        Column(modifier = Modifier.weight(1f).padding(16.dp)) {
            BookingStepperPayment()
            Spacer(modifier = Modifier.height(24.dp))
            InfoTextRow(stringResource(R.string.full_name_label), userName)
            InfoTextRow(stringResource(R.string.phone_number_label), userPhone)
            InfoTextRow(stringResource(R.string.email_label), userEmail)
            Spacer(modifier = Modifier.height(24.dp))
            Card(colors = CardDefaults.cardColors(containerColor = PaymentBoxBg), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PaymentRow(stringResource(R.string.ticket_price), "${formattedPrice}đ")
                    PaymentRow(stringResource(R.string.payment_fee), "0đ")
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                        Text(stringResource(R.string.default_payment_with), color = AppGreen, fontSize = 14.sp)
                        Text("${formattedPrice}đ", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color.Black)
                    }
                }
            }
        }

        // FOOTER (Nút Thanh Toán an toàn)
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = stringResource(R.string.time_remaining, timeString), color = AppGreen, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (isProcessing) return@Button
                    
                    // --- BỌC AN TOÀN CHỐNG SẬP APP ---
                    coroutineScope.launch {
                        try {
                            isProcessing = true
                            
                            // 1. Kiểm tra dữ liệu đầu vào
                            if (tripId.isBlank()) {
                                Toast.makeText(context, context.getString(R.string.error_trip_not_found), Toast.LENGTH_SHORT).show()
                                isProcessing = false
                                return@launch
                            }

                            // 2. Gọi hàm lưu Firebase và await để đảm bảo hoàn thành
                            val seatsList = selectedSeats.split(",").map { it.trim() }
                            FirestoreRepository.bookSeats(
                                tripId = tripId, 
                                newSeats = seatsList, 
                                totalPrice = totalPrice,
                                source = source,
                                destination = destination
                            )

                            // 3. Chuyển màn hình sau khi lưu thành công
                            navController.navigate("qr_code_screen/$totalPrice/$tripId")

                        } catch (e: NotImplementedError) {
                            Toast.makeText(context, context.getString(R.string.error_todo), Toast.LENGTH_LONG).show()
                            isProcessing = false
                        } catch (e: Exception) {
                            Toast.makeText(context, context.getString(R.string.error_system, e.message ?: ""), Toast.LENGTH_LONG).show()
                            e.printStackTrace()
                            isProcessing = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(stringResource(R.string.payment_step), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Components con
@Composable
fun InfoTextRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 16.sp)
        Text(value, color = Color.Black, fontSize = 16.sp)
    }
}

@Composable
fun PaymentRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = AppGreen, fontSize = 15.sp)
        Text(value, color = Color.Gray, fontSize = 15.sp)
    }
}

@Composable
fun BookingStepperPayment() {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(stringResource(R.string.time), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Text(stringResource(R.string.select_seat_step), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Text(stringResource(R.string.information), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Surface(color = AppGreen, shape = RoundedCornerShape(20.dp), modifier = Modifier.height(28.dp)) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(stringResource(R.string.payment_step), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
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