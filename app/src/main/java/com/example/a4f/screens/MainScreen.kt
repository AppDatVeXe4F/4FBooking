package com.example.a4f.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.a4f.navigation.BottomNavItem
import com.example.a4f.screens.booking.FindTripScreen

@Composable
fun MainScreen(navController: NavHostController) {

    val bottomNavController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Booking,
        BottomNavItem.MyTickets,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Tab Trang chủ
            composable(BottomNavItem.Home.route) {
                HomeScreen(navController = bottomNavController)
            }

            // 2. Tab Đặt vé (ĐÃ CHỈNH SỬA: Hứng dữ liệu và hiển thị FindTripScreen)
            composable(BottomNavItem.Booking.route) { backStackEntry ->
                // Lấy dữ liệu từ SavedStateHandle (do Trang chủ gửi sang)
                // Nếu không có (bấm trực tiếp vào tab) thì sẽ là null
                val savedStateHandle = bottomNavController.previousBackStackEntry?.savedStateHandle
                val source = savedStateHandle?.get<String>("source")
                val destination = savedStateHandle?.get<String>("destination")
                val date = savedStateHandle?.get<String>("date")

                // Hiển thị màn hình Tìm chuyến ngay tại đây
                FindTripScreen(
                    navController = bottomNavController,
                    source = source,
                    destination = destination,
                    date = date
                )
            }

            // 3. Tab Vé của tôi
            composable(BottomNavItem.MyTickets.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Vé của tôi", fontSize = 20.sp)
                }
            }

            // 4. Tab Cá nhân
            composable(BottomNavItem.Profile.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Cá nhân", fontSize = 20.sp)
                }
            }

            // ĐÃ XÓA: Đoạn composable(route = "find_trip_screen/...") vì không cần thiết nữa
        }
    }
}