package com.example.a4f.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.a4f.screens.*

object AppRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val FORGOT_PASSWORD = "forgot_password"
    const val MY_TICKETS = "my_tickets"
    const val TICKET_DETAIL = "ticket_detail"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH
    ) {
        composable(AppRoutes.SPLASH) { SplashScreen(navController) }
        composable(AppRoutes.ONBOARDING) { OnboardingScreen(navController) }
        composable(AppRoutes.LOGIN) { LoginScreen(navController) }
        composable(AppRoutes.REGISTER) { RegisterScreen(navController) }
        composable(AppRoutes.HOME) { MainScreen(navController = navController) }
        composable(AppRoutes.FORGOT_PASSWORD) { ForgotPasswordScreen(navController) }

        composable(AppRoutes.MY_TICKETS) {
            MyTicketsScreen(navController = navController)
        }

        composable("${AppRoutes.TICKET_DETAIL}/{ticketId}") { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId") ?: ""
            TicketDetailScreen(navController, ticketId)
        }
    }
}