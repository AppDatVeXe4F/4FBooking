package com.example.a4f.navigation


import androidx.navigation.NavHostController
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// import com.example.a4f.screens.HomeScreen     // <-- Dòng này không cần ở đây nữa
import com.example.a4f.screens.LoginScreen
import com.example.a4f.screens.OnboardingScreen
import com.example.a4f.screens.RegisterScreen
import com.example.a4f.screens.SplashScreen
import com.example.a4f.screens.ForgotPasswordScreen
import com.example.a4f.screens.MainScreen // <-- BƯỚC 3: THÊM IMPORT NÀY


// Định nghĩa các hằng số cho route (Giữ nguyên)
object AppRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val FORGOT_PASSWORD = "forgot_password"
    const val OTP_VERIFICATION = "otp_verification"


    const val RESET_PASSWORD = "reset_password"
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH // Bắt đầu từ màn hình Splash
    ) {


        // 1. Màn hình Splash (Giữ nguyên)
        composable(AppRoutes.SPLASH) {
            SplashScreen(navController = navController)
        }


        // 2. Màn hình Onboarding (Giữ nguyên)
        composable(AppRoutes.ONBOARDING) {
            OnboardingScreen(navController = navController)
        }


        // 3. Màn hình Đăng nhập (Giữ nguyên)
        composable(AppRoutes.LOGIN) {
            LoginScreen(navController = navController)
        }


        // 4. Màn hình Đăng ký (Giữ nguyên)
        composable(AppRoutes.REGISTER) {
            RegisterScreen(navController = navController)
        }


        // 5. MÀN HÌNH CHÍNH (BƯỚC 3: ĐÃ THAY ĐỔI)
        composable(AppRoutes.HOME) {
            MainScreen() // <-- Thay vì gọi HomeScreen, gọi MainScreen
            //     Nó đã chứa bottom bar và cả HomeScreen bên trong.
        }


        // (Giữ nguyên)
        composable(AppRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(navController = navController)
        }


    }
}


@Composable
fun OnboardingScreen(navController: NavHostController) {
    TODO("Not yet implemented")
}



