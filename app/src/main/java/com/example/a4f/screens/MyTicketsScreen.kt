@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a4f.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.a4f.R
import com.example.a4f.data.Ticket
import com.example.a4f.data.TicketListViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MyTicketsScreen(
    navController: NavHostController,
    viewModel: TicketListViewModel = viewModel()
) {
    val tickets by viewModel.tickets.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    // Refresh tickets khi màn hình được hiển thị lại
    LaunchedEffect(Unit) {
        viewModel.refreshTickets()
    }

    val tabTitles = listOf(
        stringResource(R.string.upcoming),
        stringResource(R.string.today),
        stringResource(R.string.completed),
        stringResource(R.string.cancelled)
    )
    val today = truncateTime(Date())

    val filteredTickets = tickets.filter { ticket ->
        val statusLower = ticket.status.lowercase()
        if (statusLower == "cancelled") {
            selectedTab == 3 // Chỉ hiển thị tab "Đã hủy"
        } else {
            ticket.bookedAt?.toDate()?.let { date ->
                val ticketDate = truncateTime(date)
                when (selectedTab) {
                    0 -> ticketDate.after(today)       // Sắp tới
                    1 -> ticketDate == today           // Đã tới
                    2 -> ticketDate.before(today)      // Hoàn thành
                    else -> false
                }
            } ?: false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF49736E)),
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.my_tickets), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                    }
                }
            )
        },
        containerColor = Color(0xFFEEF5F4)
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF006A60)
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            viewModel.setSelectedTab(index)
                        },
                        selectedContentColor = Color(0xFF006A60),
                        unselectedContentColor = Color.Gray,
                        text = { Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredTickets.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_tickets), color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredTickets) { ticket ->
                        TicketCard(ticket) {
                            navController.navigate("ticket_detail/${ticket.id}")
                        }
                    }
                }
            }
        }
    }
}

fun truncateTime(date: Date): Date {
    val cal = Calendar.getInstance()
    cal.time = date
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.time
}

@Composable
fun TicketCard(ticket: Ticket, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(12.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.verticalGradient(listOf(Color(0xFFE8F4F2), Color(0xFFD6ECE9))))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                    "cancelled" -> Color(0xFFFF4444)  // màu đỏ cho vé đã hủy
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
                    Text(statusText.uppercase(), color = Color(0xFF0A3D3A), fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }

                Divider(color = Color(0xFF49736E).copy(alpha = 0.4f), thickness = 1.dp)

                val textColor = Color(0xFF1B4F4A)
                Text(stringResource(R.string.date, ticket.bookedAt?.toDate()?.let { dateFormat.format(it) } ?: "-"), color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(stringResource(R.string.seats, ticket.seatNumber.joinToString(", ")), color = textColor, fontSize = 15.sp)
                Text(stringResource(R.string.total_price, ticket.totalPrice.toString()), color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
