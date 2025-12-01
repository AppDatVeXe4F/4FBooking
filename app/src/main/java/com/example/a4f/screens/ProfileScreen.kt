package com.example.a4f.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.a4f.R
import com.example.a4f.navigation.AppRoutes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    mainNavController: NavHostController? = null
) {
    val context = LocalContext.current
    val prefs = remember { 
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }
    
    // Load ngôn ngữ đã lưu, mặc định là "vi"
    var selectedLanguage by remember { 
        mutableStateOf(prefs.getString("selected_language", "vi") ?: "vi")
    }
    
    // Hàm lưu ngôn ngữ
    fun saveLanguage(language: String) {
        if (selectedLanguage == language) return // Không cần đổi nếu đã chọn
        
        selectedLanguage = language
        prefs.edit().putString("selected_language", language).apply()
        
        // Sync với Firestore
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(currentUser.uid)
                .update("language", language)
                .addOnFailureListener { /* Ignore nếu lỗi */ }
        }
        
        // Restart activity để apply locale mới
        (context as? android.app.Activity)?.recreate()
    }
    
    // Lấy string resources trước
    val loadingText = stringResource(R.string.loading)
    val newMemberText = stringResource(R.string.new_member)
    val guestText = stringResource(R.string.guest)
    
    // State cho user data
    var userName by remember { mutableStateOf(loadingText) }
    var memberType by remember { mutableStateOf(newMemberText) }
    var isLoading by remember { mutableStateOf(true) }

    // Lấy dữ liệu user từ Firestore
    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val db = FirebaseFirestore.getInstance()

        if (currentUser != null) {
            try {
                val document = db.collection("users").document(currentUser.uid).get().await()
                if (document != null && document.exists()) {
                    userName = document.getString("fullName") ?: guestText
                    memberType = newMemberText
                } else {
                    userName = currentUser.displayName ?: currentUser.email?.split("@")?.get(0) ?: guestText
                }
            } catch (e: Exception) {
                userName = currentUser.displayName ?: currentUser.email?.split("@")?.get(0) ?: guestText
            }
        } else {
            userName = guestText
        }
        isLoading = false
    }

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // Header với background teal-green - giống ảnh 2
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF49736E))
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar icon
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF9C27B0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // User info - màu trắng
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = userName,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = memberType,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }

                    // Edit icon - màu trắng
                    IconButton(onClick = {
                        // TODO: Navigate to edit profile
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            // Danh sách menu trên nền trắng
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                ProfileMenuItem(
                    icon = Icons.Default.Notifications,
                    title = stringResource(R.string.notifications),
                    onClick = {
                        navController.navigate("notifications")
                    }
                )

                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = stringResource(R.string.profile_and_settings),
                    onClick = {
                        navController.navigate("profile_settings")
                    }
                )

                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                ProfileMenuItem(
                    icon = Icons.Default.Info,
                    title = stringResource(R.string.support_center),
                    onClick = {
                        navController.navigate("support_center")
                    }
                )

                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                // Language selection
                LanguageSelector(
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = { lang -> saveLanguage(lang) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )

                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                ProfileMenuItem(
                    icon = Icons.Default.ExitToApp,
                    title = stringResource(R.string.logout),
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        // Dùng mainNavController để navigate về login nếu có, nếu không dùng navController
                        val controller = mainNavController ?: navController
                        controller.navigate(AppRoutes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    isInfoIcon: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon với background tròn cho Info icon (Trung tâm hỗ trợ)
        if (isInfoIcon) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2196F3).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(22.dp)
                )
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF49736E),
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1B4F4A),
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF9E9E9E),
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun LanguageSelector(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val vietnameseLabel = stringResource(R.string.vietnamese)
    val englishLabel = stringResource(R.string.english)
    
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Vietnamese option
        LanguageOption(
            language = "vi",
            label = vietnameseLabel,
            flagEmoji = "🇻🇳",
            isSelected = selectedLanguage == "vi",
            onClick = { onLanguageSelected("vi") },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // English option
        LanguageOption(
            language = "en",
            label = englishLabel,
            flagEmoji = "🇬🇧",
            isSelected = selectedLanguage == "en",
            onClick = { onLanguageSelected("en") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun LanguageOption(
    language: String,
    label: String,
    flagEmoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) Color(0xFFEEF5F4) // Màu xám nhạt khi được chọn
                else Color.White // Màu trắng khi không chọn
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = flagEmoji,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF49736E) // Màu teal-green đồng nhất cho cả hai
            )
        }
    }
}
