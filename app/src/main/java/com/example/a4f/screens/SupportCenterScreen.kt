package com.example.a4f.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import com.example.a4f.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportCenterScreen(navController: NavHostController) {
    val context = LocalContext.current

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
                        stringResource(R.string.support_center_title),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Section: Hướng dẫn
            Text(
                stringResource(R.string.instructions),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Hướng dẫn thanh toán
            SupportMenuItem(
                title = stringResource(R.string.payment_instructions),
                onClick = {
                    // TODO: Navigate to payment instructions
                }
            )

            // Quy chế
            SupportMenuItem(
                title = stringResource(R.string.regulations),
                onClick = {
                    // TODO: Navigate to regulations
                }
            )

            // Câu hỏi thường gặp
            SupportMenuItem(
                title = stringResource(R.string.faq),
                onClick = {
                    // TODO: Navigate to FAQ
                }
            )

            Divider(
                color = Color(0xFFE0E0E0),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // Section: Thông tin liên hệ xe khách
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    stringResource(R.string.bus_contact_info),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Hotline
                ContactInfoItem(
                    icon = Icons.Default.Phone,
                    label = stringResource(R.string.hotline),
                    value = "1900 000 000",
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:1900000000")
                        }
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email
                ContactInfoItem(
                    icon = Icons.Default.Email,
                    label = stringResource(R.string.contact_via_email),
                    value = "nhaxe4f@gmail.com",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:nhaxe4f@gmail.com")
                        }
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Social media
                Text(
                    stringResource(R.string.contact_via_social),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Facebook
                SocialMediaItem(
                    icon = Icons.Default.Share,
                    value = "facebook.com/4fxekhach",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://facebook.com/4fxekhach")
                        }
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Instagram
                SocialMediaItem(
                    icon = Icons.Default.Share,
                    value = "@4f_xe_khach_s1",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://instagram.com/4f_xe_khach_s1")
                        }
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SupportMenuItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF9E9E9E),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ContactInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF49736E),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun SocialMediaItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF49736E),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

