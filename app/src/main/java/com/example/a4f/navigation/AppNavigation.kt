package com.example.a4f.navigation


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


// IMPORT CÁC MÀN HÌNH BẠN ĐÃ CODE SẴN
import com.example.a4f.screens.LoginScreen
import com.example.a4f.screens.OnboardingScreen
import com.example.a4f.screens.RegisterScreen
import com.example.a4f.screens.SplashScreen
import com.example.a4f.screens.ForgotPasswordScreen
import com.example.a4f.screens.MainScreen


// IMPORT 3 MÀN HÌNH ĐẶT VÉ CHÍNH CỦA BẠN
import com.example.a4f.screens.booking.FindTripScreen
import com.example.a4f.screens.booking.SelectSeatScreen


object AppRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val FORGOT_PASSWORD = "forgot_password"


    // ROUTE MỚI CHO FLOW ĐẶT VÉ CỦA BẠN
    const val FIND_TRIP = "find_trip_screen"
    const val SELECT_SEAT = "select_seat_screen"
    const val FILL_INFO = "fill_info_screen"        // Sau này bạn thêm màn hình này
    const val PAYMENT = "payment_screen"            // Sau này thêm thanh toán
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH
    ) {
        composable(AppRoutes.SPLASH) {
            SplashScreen(navController = navController)
        }


        composable(AppRoutes.ONBOARDING) {
            OnboardingScreen(navController = navController)
        }


        composable(AppRoutes.LOGIN) {
            LoginScreen(navController = navController)
        }


        composable(AppRoutes.REGISTER) {
            RegisterScreen(navController = navController)
        }


        composable(AppRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(navController = navController)
        }


        // MÀN HÌNH CHÍNH (có Bottom Navigation)
        composable(AppRoutes.HOME) {
            MainScreen(navController = navController)
        }


        // 3 MÀN HÌNH ĐẶT VÉ CỦA BẠN – HOÀN HẢO 100%
        composable(AppRoutes.FIND_TRIP) {
            FindTripScreen(navController = navController)
        }


        composable(AppRoutes.SELECT_SEAT) {
            SelectSeatScreen(navController = navController)
        }

        composable("find_trip_screen") {
            FindTripScreen(navController = navController)
        }

        composable("select_seat_screen") {
            SelectSeatScreen(navController = navController)
        }


        // MÀN HÌNH TIẾP THEO (bạn sẽ làm sau)
        composable(AppRoutes.FILL_INFO) {
            // TODO: Tạo màn hình điền thông tin hành khách
            androidx.compose.material3.Text(
                text = "Đang phát triển: Điền thông tin hành khách",
                fontSize = 20.sp,
                modifier = androidx.compose.ui.Modifier.fillMaxSize().wrapContentSize()
            )
        }


        composable(AppRoutes.PAYMENT) {
            // TODO: Thanh toán Momo / ZaloPay
            androidx.compose.material3.Text(
                text = "Đang phát triển: Thanh toán",
                fontSize = 20.sp,
                modifier = androidx.compose.ui.Modifier.fillMaxSize().wrapContentSize()
            )
        }
    }
}

