// File: app/src/main/java/com/example/a4f/MyApplication.kt
package com.example.a4f


import android.app.Application
import com.google.firebase.FirebaseApp


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this)
    }
}



