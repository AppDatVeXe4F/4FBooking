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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.a4f.navigation.BottomNavItem
import com.example.a4f.screens.booking.FillInfoScreen
import com.example.a4f.screens.booking.FindTripScreen
import com.example.a4f.screens.booking.PaymentScreen
import com.example.a4f.screens.booking.SelectSeatScreen
import com.example.a4f.screens.*


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
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.route == item.route
                    } == true

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
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
            // 1. Trang chủ
            composable(BottomNavItem.Home.route) {
                HomeScreen(navController = bottomNavController)
            }

            // 2. Tab Đặt vé
            composable(BottomNavItem.Booking.route) {
                val savedState = bottomNavController.previousBackStackEntry?.savedStateHandle
                FindTripScreen(
                    navController = bottomNavController,
                    source = savedState?.get("source"),
                    destination = savedState?.get("destination"),
                    date = savedState?.get("date")
                )
            }

            // ⭐ 3. Tab Vé của tôi (MỚI)
            composable(BottomNavItem.MyTickets.route) {
                MyTicketsScreen(navController = navController)
            }

            // 4. Cá nhân
            composable(BottomNavItem.Profile.route) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Cá nhân", fontSize = 20.sp) }
            }

            /** ---------- CHỨC NĂNG ĐẶT VÉ GIỮ NGUYÊN ---------- */

            // Chọn ghế
            composable(
                route = "select_seat_screen/{tripId}/{price}/{source}/{destination}/{date}/{startTime}",
                arguments = listOf(
                    navArgument("tripId") { type = NavType.StringType },
                    navArgument("price") { type = NavType.IntType },
                    navArgument("source") { type = NavType.StringType },
                    navArgument("destination") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("startTime") { type = NavType.StringType },
                )
            ) { entry ->
                SelectSeatScreen(
                    navController = bottomNavController,
                    tripId = entry.arguments?.getString("tripId") ?: "",
                    pricePerTicket = entry.arguments?.getInt("price") ?: 0,
                    source = entry.arguments?.getString("source"),
                    destination = entry.arguments?.getString("destination"),
                    date = entry.arguments?.getString("date"),
                    startTime = entry.arguments?.getString("startTime") ?: "00:00"
                )
            }

            // Điền thông tin
            composable(
                route = "fill_info_screen/{selectedSeats}/{totalPrice}/{source}/{destination}/{date}/{startTime}/{tripId}",
                arguments = listOf(
                    navArgument("selectedSeats") { type = NavType.StringType },
                    navArgument("totalPrice") { type = NavType.IntType },
                    navArgument("source") { type = NavType.StringType },
                    navArgument("destination") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("startTime") { type = NavType.StringType },
                    navArgument("tripId") { type = NavType.StringType }
                )
            ) { entry ->
                FillInfoScreen(
                    navController = bottomNavController,
                    selectedSeats = entry.arguments?.getString("selectedSeats") ?: "",
                    totalPrice = entry.arguments?.getInt("totalPrice") ?: 0,
                    source = entry.arguments?.getString("source"),
                    destination = entry.arguments?.getString("destination"),
                    date = entry.arguments?.getString("date"),
                    startTime = entry.arguments?.getString("startTime") ?: "",
                    tripId = entry.arguments?.getString("tripId") ?: ""
                )
            }

            // Thanh toán
            composable(
                route = "payment_screen/{tripId}/{selectedSeats}/{totalPrice}/{source}/{destination}/{date}/{userName}/{userPhone}/{userEmail}",
                arguments = listOf(
                    navArgument("tripId") { type = NavType.StringType },
                    navArgument("selectedSeats") { type = NavType.StringType },
                    navArgument("totalPrice") { type = NavType.IntType },
                    navArgument("source") { type = NavType.StringType },
                    navArgument("destination") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("userName") { type = NavType.StringType },
                    navArgument("userPhone") { type = NavType.StringType },
                    navArgument("userEmail") { type = NavType.StringType }
                )
            ) { entry ->
                PaymentScreen(
                    navController = bottomNavController,
                    tripId = entry.arguments?.getString("tripId") ?: "",
                    selectedSeats = entry.arguments?.getString("selectedSeats") ?: "",
                    totalPrice = entry.arguments?.getInt("totalPrice") ?: 0,
                    source = entry.arguments?.getString("source"),
                    destination = entry.arguments?.getString("destination"),
                    date = entry.arguments?.getString("date"),
                    userName = entry.arguments?.getString("userName") ?: "",
                    userPhone = entry.arguments?.getString("userPhone") ?: "",
                    userEmail = entry.arguments?.getString("userEmail") ?: ""
                )
            }

            /// 8. MÀN HÌNH MÃ QR
            composable(
                route = "qr_code_screen/{totalPrice}/{tripId}",
                arguments = listOf(
                    navArgument("totalPrice") { type = NavType.IntType },
                    navArgument("tripId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val totalPrice = backStackEntry.arguments?.getInt("totalPrice") ?: 0
                val tripId = backStackEntry.arguments?.getString("tripId") ?: ""

                // Gọi màn hình QRCodeScreen
                // Nếu báo đỏ chữ QRCodeScreen, hãy trỏ chuột vào và bấm Alt+Enter để Import
                com.example.a4f.screens.booking.QRCodeScreen(
                    navController = bottomNavController,
                    totalPrice = totalPrice,
                    tripId = tripId
                )
            }
        }
    }
}
