package com.example.a4f.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home_tab", "Trang chủ", Icons.Default.Home)
    object Booking : BottomNavItem("booking_tab", "Đặt vé", Icons.Default.CalendarMonth)
    object MyTickets : BottomNavItem("tickets_tab", "Vé của tôi", Icons.Default.ConfirmationNumber)
    object Profile : BottomNavItem("profile_tab", "Cá nhân", Icons.Default.AccountCircle)
}