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
import androidx.compose.ui.res.stringResource
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
                Text(stringResource(R.string.email), fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = null },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.email_placeholder)) },
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
                Text(stringResource(R.string.full_name), fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it; fullNameError = null },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.full_name_placeholder)) },
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
                Text(stringResource(R.string.password), fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = null },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.password_placeholder)) },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = stringResource(R.string.show_hide_password)
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
                            Text(stringResource(R.string.password_requirements), color = Color.Gray, fontSize = 12.sp)
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
                            emailError = context.getString(R.string.please_enter_valid_email)
                            isValid = false
                        }

                        if (fullName.trim().isBlank()) {
                            fullNameError = context.getString(R.string.please_enter_full_name_register)
                            isValid = false
                        }

                        if (!isPasswordValid(password)) {
                            passwordError = context.getString(R.string.password_validation_error)
                            isValid = false
                        }

                        if (isValid) {
                            isLoading = true
                            coroutineScope.launch {
                                try {
                                    val authResult = auth.createUserWithEmailAndPassword(email.trim(), password).await()
                                    val firebaseUser = authResult.user ?: throw Exception(context.getString(R.string.cannot_get_user_info))
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

                                    Toast.makeText(context, context.getString(R.string.register_success, fullName), Toast.LENGTH_LONG).show()
                                    navigateToHome()


                                } catch (e: Exception) {
                                    when (e) {
                                        is com.google.firebase.auth.FirebaseAuthUserCollisionException -> {
                                            emailError = context.getString(R.string.email_already_used)
                                        }
                                        is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> {
                                            passwordError = context.getString(R.string.password_too_weak)
                                        }
                                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> {
                                            emailError = context.getString(R.string.invalid_email_format)
                                        }
                                        else -> {
                                            Toast.makeText(context, context.getString(R.string.register_failed, e.localizedMessage ?: ""), Toast.LENGTH_LONG).show()
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
                        Text(stringResource(R.string.creating_account), fontSize = 18.sp, color = Color.White)
                    } else {
                        Text(stringResource(R.string.register), fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }


                Spacer(modifier = Modifier.height(32.dp))


                Text(stringResource(R.string.or), color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))


                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    IconButton(
                        onClick = {
                            isLoading = true
                            auth.signInAnonymously()
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, context.getString(R.string.login_as_guest), Toast.LENGTH_SHORT).show()
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


