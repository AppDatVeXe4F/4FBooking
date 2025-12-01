package com.example.a4f.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.a4f.R

sealed class BottomNavItem(
    val route: String,
    val titleResId: Int,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home_tab", R.string.home, Icons.Default.Home)
    object Booking : BottomNavItem("booking_tab", R.string.booking, Icons.Default.CalendarMonth)
    object MyTickets : BottomNavItem("tickets_tab", R.string.my_tickets_tab, Icons.Default.ConfirmationNumber)
    object Profile : BottomNavItem("profile_tab", R.string.profile, Icons.Default.AccountCircle)
    
    @Composable
    fun getTitle(): String {
        return stringResource(titleResId)
    }
}