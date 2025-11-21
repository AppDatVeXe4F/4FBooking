package com.example.a4f.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a4f.data.FirestoreService // Import Service
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

val SectionHeaderBg = Color(0xFFCFE0DE)
val InfoBoxBg = Color(0xFFE0E0E0)
val TicketInfoBg = Color(0xFF9EB8B6)

@Composable
fun FillInfoScreen(
    navController: NavController,
    source: String?,
    destination: String?,
    date: String?,
    selectedSeats: String,
    totalPrice: Int,
    startTime: String,
    tripId: String
) {
    val arrivalTime = calculateArrivalTime(startTime, -30)
    val endTime = calculateArrivalTime(startTime, 240)
    val displayDate = date?.replace("-", "/") ?: ""
    val displayPrice = if (totalPrice > 0) "${totalPrice/1000}.000đ" else "0đ"
    var userName by remember { mutableStateOf("Đang tải...") }
    var userPhone by remember { mutableStateOf("Đang tải...") }
    var userEmail by remember { mutableStateOf("Đang tải...") }
    var fullPickupAddress by remember { mutableStateOf(source ?: "") }
    var fullDropOffAddress by remember { mutableStateOf(destination ?: "") }
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val db = FirebaseFirestore.getInstance()

        if (currentUser != null) {
            userEmail = currentUser.email ?: ""
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        userName = document.getString("fullName") ?: document.getString("name") ?: ""
                        userPhone = document.getString("phoneNumber") ?: document.getString("phone") ?: ""
                    } else {
                        userName = currentUser.displayName ?: ""
                    }
                }
        }

        if (source != null) fullPickupAddress = FirestoreService.getAddressByName(source)
        if (destination != null) fullDropOffAddress = FirestoreService.getAddressByName(destination)
    }

    if (showEditDialog) {
        EditInfoDialog(
            currentName = userName,
            currentPhone = userPhone,
            currentEmail = userEmail,
            onDismiss = { showEditDialog = false },
            onSave = { newName, newPhone, newEmail ->
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    val updates = mapOf(
                        "fullName" to newName,
                        "phoneNumber" to newPhone
                    )
                    FirebaseFirestore.getInstance().collection("users").document(userId).update(updates)
                }
                userName = newName
                userPhone = newPhone
                userEmail = newEmail
                showEditDialog = false
            }
        )
    }

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
                    Text(text = displayDate, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }

        // BODY
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            BookingStepperInfo()
            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 1: THÔNG TIN KHÁCH HÀNG
            SectionTitle(
                title = "Thông tin khách hàng",
                onEdit = { showEditDialog = true } //
            )
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(label = "Họ và tên :", value = userName)
                InfoRow(label = "Số điện thoại :", value = userPhone)
                InfoRow(label = "Email :", value = userEmail)
            }

            // SECTION 2 & 3 & FOOTER
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(colors = CardDefaults.cardColors(containerColor = TicketInfoBg), shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1.3f).height(70.dp)) {
                    Column(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.Center) {
                        Text("$startTime - $endTime", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("$source - $destination", color = Color.White, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 14.sp)
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(colors = CardDefaults.cardColors(containerColor = TicketInfoBg), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(31.dp)) {
                        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(displayPrice, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Icon(Icons.Default.Circle, contentDescription = null, tint = AppGreen, modifier = Modifier.size(6.dp))
                            Text("Limousine", color = Color.White, fontSize = 10.sp)
                        }
                    }
                    Card(colors = CardDefaults.cardColors(containerColor = TicketInfoBg), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(31.dp)) {
                        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Chair, contentDescription = null, tint = AppGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(selectedSeats.ifEmpty { "Chưa chọn" }, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle(title = "Thông tin đón trả", onEdit = null)
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Điểm đón", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                AddressBox(text = fullPickupAddress)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Lưu ý:", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Quý khách vui lòng có mặt tại $source trước $arrivalTime $displayDate để được kiểm tra thông tin trước khi lên xe.", color = AppGreen, fontSize = 13.sp, lineHeight = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Điểm trả", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                AddressBox(text = fullDropOffAddress)
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // FOOTER
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomCenter) {
        Button(
            onClick = {
                val safeSource = source ?: "TP. HCM"
                val safeDest = destination ?: "AN GIANG"
                val safeDate = date?.replace("/", "-") ?: ""
                navController.navigate("payment_screen/$tripId/$selectedSeats/$totalPrice/$safeSource/$safeDest/$safeDate/$userName/$userPhone/$userEmail")
            },
            colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Tiếp tục", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }
    }
}

fun calculateArrivalTime(time: String, minuteOffset: Int): String {
    return try {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = sdf.parse(time)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MINUTE, minuteOffset)
        sdf.format(calendar.time)
    } catch (e: Exception) { time }
}

@Composable
fun SectionTitle(title: String, onEdit: (() -> Unit)? = null) {
    Row(modifier = Modifier.fillMaxWidth().background(SectionHeaderBg).padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
        if (onEdit != null) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color.Black, modifier = Modifier.size(20.dp).clickable { onEdit() })
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 15.sp)
        Text(value, color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun AddressBox(text: String) {
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(InfoBoxBg).padding(12.dp)) {
        Text(text, color = Color.Black, fontSize = 14.sp)
    }
}

@Composable
fun BookingStepperInfo() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Thời gian", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            Text("Chọn ghế", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            Surface(color = AppGreen, shape = RoundedCornerShape(20.dp), modifier = Modifier.height(28.dp)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                    Text("THÔNG TIN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            Text("Thanh toán", fontSize = 12.sp, color = Color.Gray)
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AppLightGreen))
            Divider(modifier = Modifier.weight(1f).height(1.dp), color = AppGreen)
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AppLightGreen))
            Divider(modifier = Modifier.weight(1f).height(1.dp), color = AppGreen)
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(AppGreen))
            Divider(modifier = Modifier.weight(1f).height(1.dp), color = AppLightGreen)
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AppLightGreen))
        }
    }
}

@Composable
fun EditInfoDialog(
    currentName: String,
    currentPhone: String,
    currentEmail: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var phone by remember { mutableStateOf(currentPhone) }
    var email by remember { mutableStateOf(currentEmail) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SectionHeaderBg, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text("Thông tin tin khách hàng", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column {
                // Họ tên
                Text("Họ tên *", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.clickable { name = "" }) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email
                Text("Email *", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.clickable { email = "" }) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Số điện thoại
                Text("Số điện thoại *", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.clickable { phone = "" }) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, phone, email) },
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                modifier = Modifier.fillMaxWidth().height(45.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Lưu", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
            }
        }
    )
}