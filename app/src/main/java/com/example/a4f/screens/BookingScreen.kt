// trong com/example/a4f/screens/BookingScreen.kt


package com.example.a4f.screens


import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.a4f.screens.booking.FindTripScreen
import com.example.a4f.screens.booking.SelectSeatScreen


// --- BookingRoutes PHẢI NẰM Ở ĐÂY ---
object BookingRoutes {
    const val FIND_TRIP = "find_trip"
    const val SELECT_SEAT = "select_seat"
    const val FILL_INFO = "fill_info"
    const val PAYMENT = "payment"
}


@Composable
fun BookingScreen(
    mainNavController: NavHostController,
    paddingValues: PaddingValues
) {
    val bookingFlowNavController = rememberNavController()
    val navBackStackEntry by bookingFlowNavController.currentBackStackEntryAsState()


    // Logic `currentStepRoute` (quay về bản cũ)
    val currentRoute = navBackStackEntry?.destination?.route ?: BookingRoutes.FIND_TRIP


    val darkTeal = Color(0xFF425E5E)


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = paddingValues.calculateBottomPadding())
    ) {


        // --- TopBar (quay về bản cũ, không có nút back) ---
        BookingTopBar(
            departure = "TP. Hồ Chí Minh",
            destination = "An Giang",
            date = "Chủ nhật, 28/09/2025",
            backgroundColor = darkTeal
        )


        // --- Stepper (dùng currentRoute) ---
        BookingStepper(
            currentStepRoute = currentRoute,
            activeColor = darkTeal
        )


        // --- NAVHOST (quay về bản cũ) ---
        NavHost(
            navController = bookingFlowNavController,
            startDestination = BookingRoutes.FIND_TRIP,
            modifier = Modifier.weight(1f),
            // THÊM DÒNG NÀY ĐỂ RESET VỀ MÀN ĐẦU KHI VÀO LẠI TAB
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            composable(BookingRoutes.FIND_TRIP) {
                FindTripScreen(navController = bookingFlowNavController)
            }


            // Màn 2 (Không nhận tham số)
            composable(BookingRoutes.SELECT_SEAT) {
                SelectSeatScreen(navController = bookingFlowNavController)
            }


            // Màn 3
            composable(BookingRoutes.FILL_INFO) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Màn hình Điền Thông Tin")
                }
            }


            // Màn 4
            composable(BookingRoutes.PAYMENT) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Màn hình Thanh Toán")
                }
            }
        } // <-- Dấu } của NavHost
    } // <-- Dấu } của Column
} // <-- Dấu } của fun BookingScreen

