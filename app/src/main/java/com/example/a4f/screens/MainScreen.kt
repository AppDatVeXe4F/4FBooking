// trong com/example/a4f/screens/MainScreen.kt


package com.example.a4f.screens


import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.a4f.navigation.BottomNavItem


// (Bạn sẽ cần tạo các file này ở Bước 4)
// import com.example.a4f.screens.tabs.BookingScreen
// import com.example.a4f.screens.tabs.MyTicketsScreen
// import com.example.a4f.screens.tabs.ProfileScreen




@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val bottomNavController = rememberNavController() // NavController cho các tab


    val screens = listOf(
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


                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            bottomNavController.navigate(screen.route) {
                                // Pop up to the start destination to avoid building up a large stack
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid re-launching the same destination
                                launchSingleTop = true
                                // Restore state when re-selecting
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // NavHost nội bộ cho 4 màn hình chính
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,
            //modifier = Modifier.padding(innerPadding) // Thêm padding nếu cần
        ) {
            // 1. Trang chủ (dùng lại HomeScreen bạn đã tạo)
            composable(BottomNavItem.Home.route) {
                HomeScreen(navController = bottomNavController) // Dùng lại HomeScreen của bạn
            }


            // 2. Đặt vé
            composable(BottomNavItem.Booking.route) {
                BookingScreen(mainNavController = bottomNavController, paddingValues = innerPadding)
            }


            // 3. Vé của tôi
            composable(BottomNavItem.MyTickets.route) {
                // MyTicketsScreen(navController = bottomNavController)
                Text("Màn hình Vé Của Tôi") // Placeholder
            }


            // 4. Cá nhân
            composable(BottomNavItem.Profile.route) {
                // ProfileScreen(navController = bottomNavController)
                Text("Màn hình Cá Nhân") // Placeholder
            }
        }
    }
}



