package com.example.a4f.screens




import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward // Icon mũi tên
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.a4f.R
import com.example.a4f.navigation.AppRoutes
import com.example.a4f.ui.theme.AppBackgroundColor // Import màu nền xanh
import kotlinx.coroutines.launch




@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {




    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()




    // Hàm xử lý khi nhấn nút "Next"
    val onNextClick = {
        if (pagerState.currentPage < 1) {
            scope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        } else {
            navController.navigate(AppRoutes.LOGIN) {
                popUpTo(AppRoutes.ONBOARDING) { inclusive = true }
            }
        }
    }




    val onSkipClick = {
        navController.navigate(AppRoutes.LOGIN) {
            popUpTo(AppRoutes.ONBOARDING) { inclusive = true }
        }
    }




    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundColor)
    ) {
        // 2. Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> OnboardingPage(
                    imageRes = R.drawable.onboarding_1,
                    title = "DÙ ĐI MUÔN NƠI",
                    description = "Đặt vé thảnh thơi, không lo về giá!!!\nMời bạn ghé nhaaaaa"
                )
                1 -> OnboardingPage(
                    imageRes = R.drawable.onboarding_2,
                    title = "CHỌN GHẾ ĐÚNG GU",
                    description = "Ví vu mọi lúc"
                )
            }
        }




        // 3. Thanh điều khiển ở dưới
        BottomControls(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp, start = 24.dp, end = 24.dp),
            pagerState = pagerState,
            pageCount = 2,
            onSkipClick = onSkipClick,
            onNextClick = onNextClick as () -> Unit,
        )
    }
}




// Composable riêng cho thanh điều khiển
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BottomControls(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    pageCount: Int,
    onSkipClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Skip
        TextButton(onClick = onSkipClick) {
            Text(text = "Skip", color = Color.White.copy(alpha = 0.7f))
        }




        // 2. Dấu chấm
        MyPagerIndicator(
            pagerState = pagerState,
            pageCount = pageCount,
            activeColor = Color.White,
            inactiveColor = Color.White.copy(alpha = 0.3f)
        )




        // 3. Next
        IconButton(
            onClick = onNextClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.White,
                contentColor = AppBackgroundColor
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Tiếp theo"
            )
        }
    }
}




// Composable cho Dấu chấm (Indicator)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyPagerIndicator(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier,
    activeColor: Color,
    inactiveColor: Color
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { iteration ->
            val color = if (pagerState.currentPage == iteration) activeColor else inactiveColor
            val width = if (pagerState.currentPage == iteration) 24.dp else 8.dp




            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(width = width, height = 8.dp)
            )
        }
    }
}



