package com.example.a4f.screens


import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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


import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


// Hàm kiểm tra độ mạnh mật khẩu
private fun isPasswordValid(password: String): Boolean {
    val hasMinLength = password.length >= 8
    val hasDigit = password.any { it.isDigit() }
    val hasSpecialChar = password.any { !it.isLetterOrDigit() }
    return hasMinLength && hasDigit && hasSpecialChar
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {


    // --- Biến State (Giao diện) ---
    var email by rememberSaveable { mutableStateOf("") }
    var tenKhachHang by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }


    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var tenKhachHangError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }


    // --- Biến (Logic Firebase) ---
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }


    // --- Hàm hỗ trợ (Cho Google/Ẩn danh) ---
    fun navigateToHome() {
        navController.navigate(AppRoutes.HOME) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }


    // --- Cấu hình Google Sign-In ---
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }


    // --- Launcher của Google ---
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val idToken = account.idToken!!
                val credential = GoogleAuthProvider.getCredential(idToken, null)


                coroutineScope.launch {
                    try {
                        isLoading = true
                        auth.signInWithCredential(credential).await()
                        Toast.makeText(context, "Đăng nhập Google thành công", Toast.LENGTH_SHORT).show()
                        navigateToHome() // Đăng nhập Google vào thẳng Home
                    } catch (e: Exception) {
                        Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isLoading = false
                    }
                }
            } catch (e: ApiException) {
                isLoading = false
                Toast.makeText(context, "Đăng nhập Google thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            isLoading = false
        }
    }


    // --- GIAO DIỆN CHÍNH ---
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Quay lại"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
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
                    contentDescription = "Register Van",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp, start = 16.dp, end = 16.dp),
                    contentScale = ContentScale.Fit
                )


                Spacer(modifier = Modifier.height(32.dp))


                // Ô "Email"
                Text(
                    text = "Email",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("yourname@gmail.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(16.dp),
                    isError = emailError != null,
                    supportingText = {
                        if (emailError != null) {
                            Text(text = emailError!!, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )


                Spacer(modifier = Modifier.height(16.dp))


                // Ô "Tên Khách Hàng"
                Text(
                    text = "Tên Khách Hàng",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = tenKhachHang,
                    onValueChange = {
                        tenKhachHang = it
                        tenKhachHangError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Họ và Tên") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(16.dp),
                    isError = tenKhachHangError != null,
                    supportingText = {
                        if (tenKhachHangError != null) {
                            Text(text = tenKhachHangError!!, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )


                Spacer(modifier = Modifier.height(16.dp))


                // Ô "Password"
                Text(
                    text = "Password",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("•••••••••") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = "Toggle password visibility")
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(16.dp),
                    isError = passwordError != null,
                    supportingText = {
                        if (passwordError != null) {
                            Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
                        } else {
                            Text(text = "Ít nhất 8 ký tự, 1 số, 1 ký tự đặc biệt.")
                        }
                    }
                )


                Spacer(modifier = Modifier.height(24.dp))


                // --- Nút "Đăng ký" (ĐÃ SỬA LOGIC) ---
                Button(
                    onClick = {
                        var isValid = true
                        if (!email.contains("@") || email.isBlank()) {
                            emailError = "Email không hợp lệ."
                            isValid = false
                        }
                        if (tenKhachHang.isBlank()) {
                            tenKhachHangError = "Vui lòng nhập họ và tên."
                            isValid = false
                        }
                        if (!isPasswordValid(password)) {
                            passwordError = "Mật khẩu không đủ mạnh."
                            isValid = false
                        }


                        if (isValid) {
                            isLoading = true
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task: Task<AuthResult> ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        // TODO: Lưu 'tenKhachHang' vào Firestore
                                        Toast.makeText(context, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show()


                                        // --- SỬA LẠI: Chuyển về trang Login ---
                                        navController.navigate(AppRoutes.LOGIN) {
                                            popUpTo(AppRoutes.REGISTER) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                        // --- HẾT SỬA ---


                                    } else {
                                        Toast.makeText(context, "Lỗi: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LoginButtonColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "Đăng ký", fontSize = 18.sp, color = Color.White)
                }


                Spacer(modifier = Modifier.height(32.dp))


                // --- Đăng nhập Social ---
                Text(text = "Or sign up with", color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nút Google (Giữ nguyên logic vào Home)
                    IconButton(
                        onClick = {
                            isLoading = true
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        },
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google Login",
                            modifier = Modifier.size(32.dp)
                        )
                    }


                    Spacer(modifier = Modifier.width(32.dp))


                    // Nút Ẩn danh (Giữ nguyên logic vào Home)
                    IconButton(
                        onClick = {
                            isLoading = true
                            auth.signInAnonymously()
                                .addOnCompleteListener { task: Task<AuthResult> ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Đăng nhập với tư cách Khách", Toast.LENGTH_SHORT).show()
                                        navigateToHome()
                                    } else {
                                        Toast.makeText(context, "Lỗi: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        },
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Đăng nhập Ẩn danh",
                            modifier = Modifier.size(32.dp),
                            tint = Color.DarkGray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }


        // --- Hiển thị Loading ---
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}



