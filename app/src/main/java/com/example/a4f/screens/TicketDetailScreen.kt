@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.a4f.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.a4f.data.Ticket
import com.example.a4f.data.TicketListViewModel
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.shape.CircleShape


@Composable
fun TicketDetailScreen(
    navController: NavHostController,
    ticketId: String,
    viewModel: TicketListViewModel = viewModel()
) {
    val tickets by viewModel.tickets.collectAsState()
    val ticket = tickets.find { it.id == ticketId }

    if (ticket == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Không tìm thấy vé", color = Color.Gray, fontSize = 16.sp)
        }
        return
    }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val isPaid = true
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết vé", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                    contentDescription = "QR Code",
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
                        // Status giống TicketCard
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
                                            else -> Color.Gray
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                ticket.status.uppercase(),
                                color = Color(0xFF0A3D3A),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Divider(color = Color(0xFF49736E).copy(alpha = 0.4f), thickness = 1.dp)

                        val textColor = Color(0xFF1B4F4A)
                        TicketInfoRow("Ngày", ticket.bookedAt?.toDate()?.let { dateFormat.format(it) } ?: "-", textColor)
                        TicketInfoRow("Ghế", ticket.seatNumber.joinToString(", "), textColor)
                        TicketInfoRow("Tổng tiền", "${ticket.totalPrice} VND", textColor, bold = true)

                        val paymentText = if (isPaid) "Đã thanh toán" else "Chưa thanh toán"
                        val paymentColor = if (isPaid) Color(0xFF2E7D32) else Color(0xFFF9A825)
                        TicketInfoRow("Trạng thái thanh toán", paymentText, paymentColor, bold = true)
                    }
                }
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
