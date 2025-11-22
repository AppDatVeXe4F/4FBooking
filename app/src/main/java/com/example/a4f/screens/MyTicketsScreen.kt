package com.example.a4f.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.a4f.data.Ticket
import com.example.a4f.data.TicketListViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTicketsScreen(
    navController: NavHostController,
    viewModel: TicketListViewModel = viewModel()
) {
    val tickets by viewModel.tickets.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Sắp tới", "Đã tới", "Hoàn thành")

    // Bỏ giờ phút giây để filter vé chính xác
    fun truncateTime(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    val today = truncateTime(Date())

    val filteredTickets = tickets.filter { ticket ->
        ticket.bookedAt?.toDate()?.let { date ->
            val ticketDate = truncateTime(date)
            when (selectedTab) {
                0 -> ticketDate.after(today)
                1 -> ticketDate == today
                2 -> ticketDate.before(today)
                else -> true
            }
        } ?: false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF49736E)),
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "VÉ CỦA TÔI",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFFEEF5F4)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Tab trạng thái
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF006A60),
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        selectedContentColor = Color(0xFF006A60), // màu xanh khi được chọn
                        unselectedContentColor = Color.Gray,       // màu xám khi không chọn
                        text = { Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // List vé
            if (filteredTickets.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Không có vé nào.", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredTickets) { ticket ->
                        TicketCard(ticket)
                    }
                }
            }
        }
    }
}

@Composable
fun TicketCard(ticket: Ticket) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(12.dp, RoundedCornerShape(18.dp)),  // tăng độ nổi
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // nền sáng
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE8F4F2),   // xanh pastel rất nhạt → sáng
                            Color(0xFFD6ECE9)    // xanh pastel đậm hơn chút
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                // --- STATUS ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(
                                when (ticket.status.lowercase()) {
                                    "confirmed" -> Color(0xFF2E7D32)  // xanh đậm rõ
                                    "pending" -> Color(0xFFF9A825)    // vàng nổi bật
                                    "cancelled" -> Color(0xFFD32F2F)  // đỏ đậm rõ
                                    else -> Color.Gray
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        ticket.status.uppercase(),
                        color = Color(0xFF0A3D3A), // xanh đậm
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Divider(color = Color(0xFF49736E).copy(alpha = 0.4f), thickness = 1.dp)

                // --- INFO TEXT ---
                val textColor = Color(0xFF1B4F4A) // xanh chữ đậm, nổi bật

                Text(
                    "Ngày: ${ticket.bookedAt?.toDate()?.let { dateFormat.format(it) } ?: "-"}",
                    color = textColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "Tuyến: ${ticket.source} → ${ticket.destination}",
                    color = textColor,
                    fontSize = 15.sp
                )
                Text(
                    "Ghế: ${ticket.seatNumber.joinToString(", ")}",
                    color = textColor,
                    fontSize = 15.sp
                )
                Text(
                    "Tổng tiền: ${ticket.totalPrice} VND",
                    color = textColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
