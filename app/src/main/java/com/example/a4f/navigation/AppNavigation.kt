package com.example.a4f.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.a4f.screens.LoginScreen
import com.example.a4f.screens.OnboardingScreen
import com.example.a4f.screens.RegisterScreen
import com.example.a4f.screens.SplashScreen
import com.example.a4f.screens.ForgotPasswordScreen
import com.example.a4f.screens.MainScreen

object AppRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val FORGOT_PASSWORD = "forgot_password"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH
    ) {
        composable(AppRoutes.SPLASH) { SplashScreen(navController = navController) }
        composable(AppRoutes.ONBOARDING) { OnboardingScreen(navController = navController) }
        composable(AppRoutes.LOGIN) { LoginScreen(navController = navController) }
        composable(AppRoutes.REGISTER) { RegisterScreen(navController = navController) }

        // MÀN HÌNH CHÍNH
        composable(AppRoutes.HOME) {
            // Truyền đúng tham số navController
            MainScreen(navController = navController)
        }

        composable(AppRoutes.FORGOT_PASSWORD) { ForgotPasswordScreen(navController = navController) }
    }
}

// Placeholder Onboarding
@Composable
fun OnboardingScreen(navController: NavHostController) {
    androidx.compose.material3.Text("Onboarding")
}