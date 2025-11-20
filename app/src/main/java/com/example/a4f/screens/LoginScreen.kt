// File: app/src/main/java/com/example/a4f/screens/LoginScreen.kt
package com.example.a4f.screens


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a4f.R
import com.example.a4f.navigation.AppRoutes
import com.example.a4f.ui.theme.LoginButtonColor
import com.example.a4f.ui.theme.LoginScreenBackground
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {


    // --- State ---
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val coroutineScope = rememberCoroutineScope()


    // Điều hướng về Home khi đăng nhập thành công
    fun navigateToHome() {
        navController.navigate(AppRoutes.HOME) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
        }
    }


    // --- GIAO DIỆN ---
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LoginScreenBackground)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Image(
                painter = painterResource(id = R.drawable.img_login_bus),
                contentDescription = "Login Illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentScale = ContentScale.Fit
            )


            Text(
                text = "Xin Chào, Quý Khách!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = LoginButtonColor,
                modifier = Modifier.padding(top = 24.dp, bottom = 32.dp)
            )


            // Email
            Text("Email", fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("yourname@gmail.com") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )


            Spacer(modifier = Modifier.height(16.dp))


            // Mật khẩu
            Text("Mật khẩu", fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("••••••••") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )


            TextButton(
                onClick = { navController.navigate(AppRoutes.FORGOT_PASSWORD) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Quên mật khẩu?", color = Color.Gray)
            }


            Spacer(modifier = Modifier.height(24.dp))


            // NÚT ĐĂNG NHẬP
            Button(
                onClick = {
                    if (email.trim().isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_LONG).show()
                        return@Button
                    }


                    isLoading = true
                    coroutineScope.launch {
                        try {
                            auth.signInWithEmailAndPassword(email.trim(), password).await()
                            Toast.makeText(context, "Chào mừng trở lại!", Toast.LENGTH_SHORT).show()
                            navigateToHome()
                        } catch (e: Exception) {
                            when (e) {
                                is FirebaseAuthInvalidUserException -> {
                                    Toast.makeText(
                                        context,
                                        "Email này chưa được đăng ký.\nVui lòng nhấn \"Đăng ký ngay\" để tạo tài khoản mới.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                is FirebaseAuthInvalidCredentialsException -> {
                                    Toast.makeText(context, "Mật khẩu không đúng. Vui lòng thử lại.", Toast.LENGTH_LONG).show()
                                }
                                else -> {
                                    Toast.makeText(context, "Lỗi kết nối. Vui lòng kiểm tra mạng và thử lại.", Toast.LENGTH_LONG).show()
                                }
                            }
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LoginButtonColor),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Đang đăng nhập...", color = Color.White, fontSize = 18.sp)
                } else {
                    Text("Đăng nhập", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }


            Spacer(modifier = Modifier.height(32.dp))


            Text("Hoặc", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))


            // Nút đăng nhập khách (ẩn danh)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                IconButton(
                    onClick = {
                        isLoading = true
                        auth.signInAnonymously()
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Đăng nhập với tư cách khách", Toast.LENGTH_SHORT).show()
                                    navigateToHome()
                                } else {
                                    Toast.makeText(context, "Lỗi kết nối, vui lòng thử lại", Toast.LENGTH_SHORT).show()
                                }
                            }
                    },
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.White)
                ) {
                    Icon(Icons.Default.HelpOutline, contentDescription = "Khách", tint = Color.DarkGray, modifier = Modifier.size(36.dp))
                }
            }


            Spacer(modifier = Modifier.height(40.dp))


            // Dòng mời đăng ký
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Chưa có tài khoản? ", color = Color.Gray)
                TextButton(onClick = { navController.navigate(AppRoutes.REGISTER) }) {
                    Text("Đăng ký ngay", color = LoginButtonColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }


            Spacer(modifier = Modifier.height(32.dp))
        }


        // Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

