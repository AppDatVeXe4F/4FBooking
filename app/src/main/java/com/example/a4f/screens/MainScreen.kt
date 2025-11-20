package com.example.a4f.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.a4f.screens.booking.SelectSeatScreen

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

            // 5. MÀN HÌNH CHỌN GHẾ (SỬA LẠI: Thêm startTime)
            composable(
                route = "select_seat_screen/{tripId}/{price}/{source}/{destination}/{date}/{startTime}", // <--- THÊM startTime
                arguments = listOf(
                    navArgument("tripId") { type = NavType.StringType },
                    navArgument("price") { type = NavType.IntType },
                    navArgument("source") { type = NavType.StringType },
                    navArgument("destination") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("startTime") { type = NavType.StringType } // <--- THÊM DÒNG NÀY
                )
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId") ?: "1"
                val price = backStackEntry.arguments?.getInt("price") ?: 0
                val source = backStackEntry.arguments?.getString("source")
                val destination = backStackEntry.arguments?.getString("destination")
                val date = backStackEntry.arguments?.getString("date")
                val startTime = backStackEntry.arguments?.getString("startTime") ?: "00:00" // <--- LẤY GIỜ

                SelectSeatScreen(
                    navController = bottomNavController,
                    tripId = tripId,
                    pricePerTicket = price,
                    source = source,
                    destination = destination,
                    date = date,
                    startTime = startTime // <--- TRUYỀN VÀO
                )
            }

            // 6. MÀN HÌNH ĐIỀN THÔNG TIN (SỬA LẠI: Thêm startTime)
            composable(
                route = "fill_info_screen/{selectedSeats}/{totalPrice}/{source}/{destination}/{date}/{startTime}", // <--- THÊM startTime
                arguments = listOf(
                    navArgument("selectedSeats") { type = NavType.StringType },
                    navArgument("totalPrice") { type = NavType.IntType },
                    navArgument("source") { type = NavType.StringType },
                    navArgument("destination") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("startTime") { type = NavType.StringType } // <--- THÊM DÒNG NÀY
                )
            ) { backStackEntry ->
                val selectedSeats = backStackEntry.arguments?.getString("selectedSeats") ?: ""
                val totalPrice = backStackEntry.arguments?.getInt("totalPrice") ?: 0
                val source = backStackEntry.arguments?.getString("source")
                val destination = backStackEntry.arguments?.getString("destination")
                val date = backStackEntry.arguments?.getString("date")
                val startTime = backStackEntry.arguments?.getString("startTime") ?: "00:00" // <--- LẤY GIỜ

                FillInfoScreen(
                    navController = bottomNavController,
                    source = source,
                    destination = destination,
                    date = date,
                    selectedSeats = selectedSeats,
                    totalPrice = totalPrice,
                    startTime = startTime // <--- TRUYỀN VÀO
                )
            }
        }
    }
}