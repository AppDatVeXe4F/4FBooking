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
        if (pagerState.currentPage < 1) { // 1 là trang cuối (vì có 2 trang 0, 1)
            // Nếu chưa phải trang cuối, vuốt sang trang kế
            scope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        } else {
            // Nếu đang ở trang cuối, chuyển đến màn hình Login
            navController.navigate(AppRoutes.LOGIN) {
                popUpTo(AppRoutes.ONBOARDING) { inclusive = true }
            }
        }
    }

    // Hàm xử lý khi nhấn "Skip"
    val onSkipClick = {
        navController.navigate(AppRoutes.LOGIN) {
            popUpTo(AppRoutes.ONBOARDING) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundColor) // 1. Nền xanh cho toàn màn hình
    ) {
        // 2. Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // Gọi Composable OnboardingPage
            when (page) {
                0 -> OnboardingPage(
                    imageRes = R.drawable.onboarding_1, // Tên ảnh 1
                    title = "DÙ ĐI MUÔN NƠI",
                    description = "Đặt vé thảnh thơi, không lo về giá!!!\nMời bạn ghé nhaaaaa"
                )
                1 -> OnboardingPage(
                    imageRes = R.drawable.onboarding_2, // Tên ảnh 2
                    title = "CHỌN GHẾ ĐÚNG GU",
                    description = "Ví vu mọi lúc" // Sửa lại mô tả cho giống design
                )
            }
        }

        // 3. Thanh điều khiển ở dưới (Skip, Dots, Next)
        BottomControls(
            modifier = Modifier
                .align(Alignment.BottomCenter) // Nằm ở dưới cùng
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
        horizontalArrangement = Arrangement.SpaceBetween // Căn 3 cụm ra 3 góc
    ) {
        // 1. Nút "Skip"
        TextButton(onClick = onSkipClick) {
            Text(text = "Skip", color = Color.White.copy(alpha = 0.7f))
        }

        // 2. Dấu chấm (Indicator)
        MyPagerIndicator(
            pagerState = pagerState,
            pageCount = pageCount,
            activeColor = Color.White, // Màu chấm (trang hiện tại)
            inactiveColor = Color.White.copy(alpha = 0.3f) // Màu chấm (trang khác)
        )

        // 3. Nút "Next" (hình tròn)
        IconButton(
            onClick = onNextClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.White, // Nền nút màu trắng
                contentColor = AppBackgroundColor // Màu mũi tên là màu nền xanh
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Tiếp theo"
            )
        }
    }
}

// Composable cho Dấu chấm (Indicator) - đã cập nhật
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
            val width = if (pagerState.currentPage == iteration) 24.dp else 8.dp // <-- Đây!

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape) // Bo tròn
                    .background(color)
                    .size(width = width, height = 8.dp) // Kích thước động
            )
        }
    }
}