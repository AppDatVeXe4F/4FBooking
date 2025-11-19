package com.example.a4f.screens




import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.a4f.R
import com.example.a4f.navigation.AppRoutes
import com.example.a4f.ui.theme.AppBackgroundColor
import kotlinx.coroutines.delay




@Composable
fun SplashScreen(navController: NavController) {




    LaunchedEffect(key1 = true) {
        delay(2000L)
        navController.navigate(AppRoutes.ONBOARDING) {
            popUpTo(AppRoutes.SPLASH) { inclusive = true }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo4f),
            contentDescription = "Logo ứng dụng 4F",


            modifier = Modifier.size(200.dp)
        )
    }
}