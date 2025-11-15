package com.example.a4f.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector


// Định nghĩa các đối tượng cho Bottom Navigation
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home_tab",
        title = "Trang chủ",
        icon = Icons.Default.Home
    )
    object Booking : BottomNavItem(
        route = "booking_tab",
        title = "Đặt vé",
        icon = Icons.Default.CalendarMonth
    )
    object MyTickets : BottomNavItem(
        route = "tickets_tab",
        title = "Vé của tôi",
        icon = Icons.Default.ConfirmationNumber
    )
    object Profile : BottomNavItem(
        route = "profile_tab",
        title = "Cá nhân",
        icon = Icons.Default.AccountCircle
    )
}



