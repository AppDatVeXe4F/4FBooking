

package com.example.a4f.screens


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a4f.R
import com.example.a4f.navigation.AppRoutes
import com.example.a4f.ui.theme.LoginButtonColor
import com.example.a4f.ui.theme.LoginScreenBackground
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.foundation.background        // ← FIX LỖI background
import kotlinx.coroutines.tasks.await               // ← FIX LỖI await


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(navController: NavController, oobCode: String?) {


    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }


    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()


    // Kiểm tra link có hợp lệ không
    LaunchedEffect(oobCode) {
        if (oobCode.isNullOrBlank()) {
            Toast.makeText(context, "Link không hợp lệ hoặc đã hết hạn!", Toast.LENGTH_LONG).show()
            navController.navigate(AppRoutes.LOGIN) { popUpTo(0) }
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Đặt lại mật khẩu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = LoginScreenBackground
    ) { paddingValues ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .consumeWindowInsets(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Image(
                painter = painterResource(R.drawable.img_login_bus),
                contentDescription = null,
                modifier = Modifier.size(180.dp).padding(top = 40.dp),
                contentScale = ContentScale.Fit
            )


            Text(
                text = "Tạo mật khẩu mới",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = LoginButtonColor,
                modifier = Modifier.padding(top = 32.dp)
            )


            Text(
                text = "Mật khẩu phải có ít nhất 6 ký tự",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 12.dp)
            )


            Spacer(Modifier.height(48.dp))


            // Mật khẩu mới
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it; error = null },
                label = { Text("Mật khẩu mới") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )


            Spacer(Modifier.height(16.dp))


            // Xác nhận mật khẩu
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Xác nhận mật khẩu") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                isError = error != null,
                supportingText = { error?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )


            Spacer(Modifier.height(40.dp))


            Button(
                onClick = {
                    when {
                        newPassword.length < 6 -> error = "Mật khẩu phải từ 6 ký tự trở lên"
                        newPassword != confirmPassword -> error = "Mật khẩu xác nhận không khớp"
                        else -> {
                            isLoading = true
                            scope.launch {
                                try {
                                    auth.confirmPasswordReset(oobCode!!, newPassword).await()
                                    Toast.makeText(context, "Đặt lại mật khẩu thành công!\nBạn có thể đăng nhập ngay.", Toast.LENGTH_LONG).show()
                                    navController.navigate(AppRoutes.LOGIN) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                } catch (e: Exception) {
                                    error = "Link đã hết hạn hoặc không hợp lệ"
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LoginButtonColor),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Đang xử lý...")
                } else {
                    Text("Xác nhận", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }


            Spacer(Modifier.height(40.dp))
        }


        // Loading overlay
        if (isLoading) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(0.4f)), Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}



