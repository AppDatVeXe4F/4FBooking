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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a4f.R
import com.example.a4f.navigation.AppRoutes
import com.example.a4f.ui.theme.LoginButtonColor
import com.example.a4f.ui.theme.LoginScreenBackground
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


// Kiểm tra độ mạnh mật khẩu
private fun isPasswordValid(password: String): Boolean {
    return password.length >= 8 &&
            password.any { it.isDigit() } &&
            password.any { !it.isLetterOrDigit() }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {


    // --- State giao diện ---
    var email by rememberSaveable { mutableStateOf("") }
    var fullName by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }


    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var fullNameError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }


    // --- State logic ---
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }


    // Điều hướng về Home khi đã đăng nhập thành công
    fun navigateToHome() {
        navController.navigate(AppRoutes.HOME) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
        }
    }


    // --- GIAO DIỆN ---
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = LoginScreenBackground
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Image(
                    painter = painterResource(id = R.drawable.img_register_van),
                    contentDescription = "Register Illustration",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentScale = ContentScale.Fit
                )


                Spacer(modifier = Modifier.height(32.dp))


                // Email
                Text("Email", fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                Spacer(modifier = Modifier.height(8.dp))
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


                Spacer(modifier = Modifier.height(16.dp))


                // Họ và tên
                Text("Họ và tên", fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it; fullNameError = null },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Nguyễn Văn A") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    isError = fullNameError != null,
                    supportingText = { fullNameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                )


                Spacer(modifier = Modifier.height(16.dp))


                // Mật khẩu
                Text("Mật khẩu", fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = null },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Hiện/Ẩn mật khẩu"
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    isError = passwordError != null,
                    supportingText = {
                        if (passwordError != null) {
                            Text(passwordError!!, color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("Tối thiểu 8 ký tự, có số và ký tự đặc biệt", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                )


                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        emailError = null
                        fullNameError = null
                        passwordError = null


                        var isValid = true

                        if (email.trim().isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                            emailError = "Vui lòng nhập email hợp lệ"
                            isValid = false
                        }

                        if (fullName.trim().isBlank()) {
                            fullNameError = "Vui lòng nhập họ và tên"
                            isValid = false
                        }

                        if (!isPasswordValid(password)) {
                            passwordError = "Mật khẩu phải ≥ 8 ký tự, có chữ số và ký tự đặc biệt"
                            isValid = false
                        }

                        if (isValid) {
                            isLoading = true
                            coroutineScope.launch {
                                try {
                                    val authResult = auth.createUserWithEmailAndPassword(email.trim(), password).await()
                                    val firebaseUser = authResult.user ?: throw Exception("Không thể lấy thông tin người dùng")
                                    val userData = hashMapOf(
                                        "fullName" to fullName.trim(),
                                        "email" to email.trim().lowercase(),
                                        "phone" to "",
                                        "createdAt" to FieldValue.serverTimestamp(),
                                        "uid" to firebaseUser.uid
                                    )


                                    firestore.collection("users")
                                        .document(firebaseUser.uid)
                                        .set(userData, SetOptions.merge())
                                        .await()

                                    Toast.makeText(context, "Đăng ký thành công! Chào mừng $fullName", Toast.LENGTH_LONG).show()
                                    navigateToHome()


                                } catch (e: Exception) {
                                    when (e) {
                                        is com.google.firebase.auth.FirebaseAuthUserCollisionException -> {
                                            emailError = "Email này đã được sử dụng. Vui lòng dùng email khác hoặc đăng nhập."
                                        }
                                        is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> {
                                            passwordError = "Mật khẩu quá yếu. Vui lòng thử lại."
                                        }
                                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> {
                                            emailError = "Email không hợp lệ. Vui lòng kiểm tra lại."
                                        }
                                        else -> {
                                            Toast.makeText(context, "Đăng ký thất bại: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LoginButtonColor),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Đang tạo tài khoản...", fontSize = 18.sp, color = Color.White)
                    } else {
                        Text("Đăng ký", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }


                Spacer(modifier = Modifier.height(32.dp))


                Text("Hoặc", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))


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
                                        Toast.makeText(context, "Lỗi: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(
                            Icons.Default.HelpOutline,
                            contentDescription = "Đăng nhập khách",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }


                Spacer(modifier = Modifier.height(32.dp))
            }
        }


        // Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}


