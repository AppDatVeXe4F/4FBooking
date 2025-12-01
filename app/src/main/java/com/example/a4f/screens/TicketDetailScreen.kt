@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.a4f.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.a4f.R
import com.example.a4f.data.TicketListViewModel
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TicketDetailScreen(
    navController: NavHostController,
    ticketId: String,
    viewModel: TicketListViewModel = viewModel()
) {
    val tickets by viewModel.tickets.collectAsState()
    val ticket = tickets.find { it.id == ticketId }

    var showCancelDialog by remember { mutableStateOf(false) }

    if (ticket == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.ticket_not_found), color = Color.Gray, fontSize = 16.sp)
        }
        return
    }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ticket_detail), fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF49736E))
            )
        },
        containerColor = Color(0xFFF0F4F3)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // QR Code
            val qrBitmap = remember(ticket.id) {
                try {
                    val encoder = BarcodeEncoder()
                    val bitMatrix = encoder.encode(ticket.id, BarcodeFormat.QR_CODE, 350, 350)
                    encoder.createBitmap(bitMatrix).asImageBitmap()
                } catch (e: Exception) {
                    null
                }
            }

            qrBitmap?.let {
                Image(
                    bitmap = it,
                    contentDescription = stringResource(R.string.qr_code),
                    modifier = Modifier
                        .size(250.dp)
                        .shadow(12.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "ĐƯA MÃ NÀY CHO NHÂN VIÊN TRƯỚC KHI LÊN XE",
                color = Color(0xFF070303),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card chi tiết vé
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .shadow(14.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFE8F4F2), Color(0xFFD6ECE9))
                            )
                        )
                        .padding(20.dp)
                        .fillMaxWidth()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Status
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (ticket.status.lowercase()) {
                                            "upcoming" -> Color(0xFF2E7D32)
                                            "today" -> Color(0xFF1976D2)
                                            "completed" -> Color.Gray
                                            "cancelled" -> Color(0xFFD32F2F)
                                            else -> Color.Gray
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
            val statusText = when (ticket.status.lowercase()) {
                "upcoming" -> stringResource(R.string.upcoming)
                "today" -> stringResource(R.string.today)
                "completed" -> stringResource(R.string.completed)
                "cancelled" -> stringResource(R.string.cancelled)
                else -> ticket.status
            }
            Text(
                statusText.uppercase(),
                color = Color(0xFF0A3D3A),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
                        }

                        Divider(color = Color(0xFF49736E).copy(alpha = 0.4f), thickness = 1.dp)

                        val textColor = Color(0xFF1B4F4A)
                        TicketInfoRow(stringResource(R.string.date_label), ticket.bookedAt?.toDate()?.let { dateFormat.format(it) } ?: "-", textColor)
                        TicketInfoRow(stringResource(R.string.seats_label), ticket.seatNumber.joinToString(", "), textColor)
                        TicketInfoRow(stringResource(R.string.total_price_label), "${ticket.totalPrice} VND", textColor, bold = true)


                        TicketInfoRow(stringResource(R.string.payment_status), stringResource(R.string.payment_status_paid), Color(0xFF2E7D32), bold = true)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nút HỦY VÉ
            if (ticket.status.lowercase() != "cancelled") {
                Button(
                    onClick = { showCancelDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(stringResource(R.string.cancel_ticket), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Dialog xác nhận hủy vé
            if (showCancelDialog) {
                val refundAmount = ticket.totalPrice * 0.9 // Hoàn 90% vì phí 10%
                AlertDialog(
                    onDismissRequest = { showCancelDialog = false },
                    title = { Text("Xác nhận", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                    text = {
                        Column {
                            Text(
                                "Bạn có chắc chắn muốn hủy vé này không?",
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Tiền của quý khách sẽ được hoàn trong 12h và phí là 10%.",
                                fontSize = 16.sp,
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Số tiền quý khách nhận được: ${"%,.0f".format(refundAmount)} VND",
                                fontSize = 18.sp,
                                color = Color.Red,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.cancelTicket(ticket.id)
                            viewModel.setSelectedTab(3)
                            showCancelDialog = false
                            navController.popBackStack()
                        }) {
                            Text("Hủy vé", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCancelDialog = false }) {
                            Text("Hủy", color = Color.Gray)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TicketInfoRow(
    title: String,
    value: String,
    valueColor: Color = Color.Black,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = Color.Black, fontWeight = FontWeight.Medium, fontSize = 15.sp)
        Text(value, color = valueColor, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, fontSize = 15.sp)
    }
}
