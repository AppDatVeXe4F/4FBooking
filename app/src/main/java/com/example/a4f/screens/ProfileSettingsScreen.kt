package com.example.a4f.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.a4f.R
import coil.compose.rememberAsyncImagePainter
import com.example.a4f.navigation.AppRoutes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    navController: NavHostController,
    mainNavController: NavHostController? = null
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val coroutineScope = rememberCoroutineScope()
    
    // State cho user data
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    
    // Date picker state
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (dateOfBirth.isNotBlank()) {
            try {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                sdf.parse(dateOfBirth)?.time
            } catch (e: Exception) {
                System.currentTimeMillis()
            }
        } else {
            System.currentTimeMillis()
        }
    )
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { avatarUri = it }
    }
    
    // Hàm convert date
    fun convertMillisToDateString(millis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(millis))
    }
    
    // Lấy dữ liệu từ Firestore
    LaunchedEffect(Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            try {
                val document = db.collection("users").document(currentUser.uid).get().await()
                if (document != null && document.exists()) {
                    fullName = document.getString("fullName") ?: ""
                    // Check cả phoneNumber và phone để tương thích với dữ liệu cũ
                    phone = document.getString("phoneNumber") ?: document.getString("phone") ?: ""
                    email = document.getString("email") ?: currentUser.email ?: ""
                    
                    // Lấy dateOfBirth nếu có (lưu dưới dạng string)
                    dateOfBirth = document.getString("dateOfBirth") ?: ""
                } else {
                    email = currentUser.email ?: ""
                }
            } catch (e: Exception) {
                email = currentUser.email ?: ""
            }
        }
        isLoading = false
    }
    
    // Hàm lưu dữ liệu
    fun saveProfile() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, context.getString(R.string.please_login), Toast.LENGTH_SHORT).show()
            return
        }
        
        if (fullName.isBlank()) {
            Toast.makeText(context, context.getString(R.string.please_enter_full_name), Toast.LENGTH_SHORT).show()
            return
        }
        
        coroutineScope.launch {
            try {
                val userData = hashMapOf(
                    "fullName" to fullName.trim(),
                    "phoneNumber" to phone.trim(), // Lưu dưới tên phoneNumber để nhất quán với FillInfoScreen
                    "phone" to phone.trim(), // Cũng lưu dưới tên phone để tương thích với dữ liệu cũ
                    "email" to email.trim()
                )
                
                // Parse dateOfBirth nếu có - lưu dưới dạng string
                if (dateOfBirth.isNotBlank()) {
                    userData["dateOfBirth"] = dateOfBirth
                }
                
                db.collection("users").document(currentUser.uid)
                    .update(userData as Map<String, Any>)
                    .await()
                
                Toast.makeText(context, context.getString(R.string.info_saved), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, context.getString(R.string.error, e.message ?: ""), Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    // Hàm xóa tài khoản
    fun deleteAccount() {
        showDeleteConfirmDialog = true
    }
    
    // Hàm thực hiện xóa tài khoản
    fun performDeleteAccount() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, context.getString(R.string.please_login), Toast.LENGTH_SHORT).show()
            return
        }
        
        isDeleting = true
        coroutineScope.launch {
            try {
                val userId = currentUser.uid
                
                // 1. Xóa user document từ Firestore
                db.collection("users").document(userId).delete().await()
                
                // 2. Xóa authentication account
                currentUser.delete().await()
                
                // 3. Hiển thị thông báo thành công
                Toast.makeText(context, context.getString(R.string.delete_account_success), Toast.LENGTH_SHORT).show()
                
                // 4. Navigate về login screen
                val controller = mainNavController ?: navController
                controller.navigate(AppRoutes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
                
            } catch (e: Exception) {
                isDeleting = false
                Toast.makeText(
                    context, 
                    context.getString(R.string.delete_account_error, e.message ?: ""), 
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF49736E))
            ) {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    
                    // Title - căn giữa
                    Text(
                        stringResource(R.string.profile_and_settings_title),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    // Logout button
                    TextButton(onClick = {
                        auth.signOut()
                        // Dùng mainNavController để navigate về login nếu có, nếu không dùng navController
                        val controller = mainNavController ?: navController
                        controller.navigate(AppRoutes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Text(
                            stringResource(R.string.logout),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        // Date picker dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            dateOfBirth = convertMillisToDateString(millis)
                        }
                    }) { 
                        Text(stringResource(R.string.ok)) 
                    }
                },
                dismissButton = { 
                    TextButton(onClick = { showDatePicker = false }) { 
                        Text(stringResource(R.string.cancel)) 
                    } 
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        
        // Delete account confirmation dialog
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { 
                    if (!isDeleting) {
                        showDeleteConfirmDialog = false 
                    }
                },
                title = {
                    Text(
                        stringResource(R.string.confirm_delete_account),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Text(
                        stringResource(R.string.delete_account_warning),
                        fontSize = 16.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteConfirmDialog = false
                            performDeleteAccount()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        enabled = !isDeleting
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text(
                                stringResource(R.string.delete_account),
                                color = Color.White
                            )
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { 
                            if (!isDeleting) {
                                showDeleteConfirmDialog = false 
                            }
                        },
                        enabled = !isDeleting
                    ) {
                        Text(
                            stringResource(R.string.cancel),
                            color = Color.Gray
                        )
                    }
                },
                containerColor = Color.White
            )
        }
        
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Avatar - có thể click để chọn ảnh
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF49736E))
                        .clickable {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(avatarUri),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = fullName.take(1).uppercase().ifEmpty { "T" },
                            color = Color.White,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.tap_to_change_photo),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Họ và tên
                Text(
                    stringResource(R.string.full_name),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color(0xFF49736E),
                        unfocusedIndicatorColor = Color(0xFFE0E0E0)
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Số điện thoại
                Text(
                    stringResource(R.string.phone_number),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Country code selector - border màu xanh đậm
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .weight(0.4f)
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        leadingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Text("🇻🇳", fontSize = 20.sp)
                                Text(stringResource(R.string.country_code), fontSize = 15.sp, color = Color.Black)
                            }
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Default.KeyboardArrowDown, 
                                null, 
                                modifier = Modifier.size(20.dp),
                                tint = Color.Black
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFF49736E),
                            unfocusedIndicatorColor = Color(0xFF49736E) // Border luôn màu xanh
                        )
                    )
                    
                    // Phone number field
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        modifier = Modifier
                            .weight(0.6f)
                            .height(56.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFF49736E),
                            unfocusedIndicatorColor = Color(0xFFE0E0E0) // Border màu xám nhạt
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Email
                Text(
                    stringResource(R.string.email),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color(0xFF49736E),
                        unfocusedIndicatorColor = Color(0xFFE0E0E0)
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Information box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF9C27B0),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            stringResource(R.string.order_info_message),
                            fontSize = 13.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Ngày sinh
                Text(
                    stringResource(R.string.date_of_birth),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    readOnly = true,
                    placeholder = { Text(stringResource(R.string.date_format_placeholder)) },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.Default.CalendarMonth, 
                                null, 
                                modifier = Modifier.size(20.dp),
                                tint = Color.Black
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color(0xFF49736E),
                        unfocusedIndicatorColor = Color(0xFFE0E0E0)
                    )
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Save button
                Button(
                    onClick = { saveProfile() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF49736E))
                ) {
                    Text(
                        stringResource(R.string.save_info),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Delete account
                TextButton(
                    onClick = { deleteAccount() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.delete_account),
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

