package com.example.a4f

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.a4f.navigation.AppNavigation // <-- 1. Import AppNavigation
import com.example.a4f.ui.theme._4FTheme // <-- 2. Thay A4FTheme bằng tên Theme của bạn

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            _4FTheme{ // <-- 3. Tên Theme của bạn
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation() // <-- 4. Gọi hàm điều hướng chính
                }
            }
        }
    }
}