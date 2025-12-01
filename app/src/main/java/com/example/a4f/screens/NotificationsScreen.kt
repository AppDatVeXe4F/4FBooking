package com.example.a4f.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import com.example.a4f.R

data class NotificationItem(
    val id: String,
    val title: String,
    val timestamp: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavHostController) {
    // Dữ liệu thông báo mẫu
    val notifications = remember {
        // Note: Notification content should come from Firestore/API in real app
        // For now, using hardcoded content as these are dynamic notifications
        listOf(
            NotificationItem(
                id = "1",
                title = "APP 4F ĐẶT NHANH – GIẢM LIỀN!",
                timestamp = "19:17 29/09/2025",
                description = "Ưu đãi liên tay: Giảm 2% vé 1 chiều, 4% vé khứ hồi khi đặt trên futabus.vn & App 4F! Áp dụng 16/09/2025 - 31/01/2026 (trừ Lễ, Tết)."
            ),
            NotificationItem(
                id = "2",
                title = "KHUYẾN MÃI ONLINE",
                timestamp = "19:17 29/09/2025",
                description = "Giảm 2% vé 1 chiều, 4% vé khứ hồi khi mua vé trực tuyến qua App 4F. Áp dụng từ 16/09/2025 - 31/01/2026, không áp dụng vào các dịp Lễ, Tết"
            ),
            NotificationItem(
                id = "3",
                title = "ƯU ĐÃI ĐẶC BIỆT CHO THÀNH VIÊN MỚI",
                timestamp = "19:17 29/09/2025",
                description = "Chào mừng thành viên mới 🎉 Nhập mã 4F30 khi đặt vé đầu tiên để nhận giảm ngay 30.000₫. Số lượng có hạn, nhanh tay nhé!"
            ),
            NotificationItem(
                id = "4",
                title = "THÔNG BÁO BẢO TRÌ HỆ THỐNG",
                timestamp = "19:17 29/09/2025",
                description = "Hệ thống sẽ bảo trì từ 23:00 30/08 đến 02:00 31/08 để nâng cấp chất lượng dịch vụ. Trong thời gian này có thể xảy ra gián đoạn khi đặt vé. Mong quý khách thông cảm 🙏"
            )
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF49736E))
            ) {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Title - căn giữa hoàn toàn
                    Text(
                        stringResource(R.string.notifications_title),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    // Back button - đặt ở bên trái
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(notifications) { notification ->
                NotificationCard(notification = notification)
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon envelope trong circle
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFEEF5F4)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = Color(0xFF49736E),
                modifier = Modifier.size(24.dp)
            )
        }

        // Nội dung thông báo
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Title
            Text(
                text = notification.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Timestamp
            Text(
                text = notification.timestamp,
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Description
            Text(
                text = notification.description,
                fontSize = 14.sp,
                color = Color(0xFF757575),
                lineHeight = 20.sp
            )
        }
    }
}

