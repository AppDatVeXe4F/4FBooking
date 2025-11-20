package com.example.a4f.screens


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a4f.R
import com.example.a4f.ui.theme.LoginButtonColor
import com.example.a4f.ui.theme.LoginScreenBackground
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {


    var email by rememberSaveable { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Quên mật khẩu", fontWeight = FontWeight.Bold) },
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
                .consumeWindowInsets(paddingValues), // ← SỬA HOÀN HẢO, KHÔNG LỖI
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Image(
                painter = painterResource(R.drawable.img_login_bus),
                contentDescription = null,
                modifier = Modifier.size(180.dp).padding(top = 32.dp),
                contentScale = ContentScale.Fit
            )


            Text(
                text = "Quên mật khẩu?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = LoginButtonColor,
                modifier = Modifier.padding(top = 32.dp)
            )


            Text(
                text = "Đừng lo lắng! Chỉ cần nhập email bạn đã dùng để đăng ký, chúng tôi sẽ gửi link đặt lại mật khẩu ngay lập tức.",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 40.dp)
            )


            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
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
                ),
                isError = emailError != null,
                supportingText = { emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )


            Spacer(Modifier.height(32.dp))


            Button(
                onClick = {
                    val emailInput = email.trim().lowercase()
                    if (emailInput.isBlank()) {
                        emailError = "Vui lòng nhập email"
                        return@Button
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                        emailError = "Email không hợp lệ"
                        return@Button
                    }


                    isLoading = true
                    scope.launch {
                        try {
                            auth.sendPasswordResetEmail(emailInput).await()
                            Toast.makeText(context, "Đã gửi link đặt lại mật khẩu!\nVui lòng kiểm tra email.", Toast.LENGTH_LONG).show()
                            navController.popBackStack()
                        } catch (e: Exception) {
                            emailError = "Email này chưa được đăng ký"
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
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Đang gửi...")
                } else {
                    Text("Gửi link đặt lại", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }


            Spacer(Modifier.height(40.dp))
        }


        if (isLoading) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(0.4f)), Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

