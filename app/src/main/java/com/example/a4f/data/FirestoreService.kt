package com.example.a4f.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreService {
    private val db = FirebaseFirestore.getInstance()

    // Hàm lấy danh sách tên địa điểm (VP Bến xe Miền Tây, VP Long Xuyên...)
    suspend fun getLocationNames(): List<String> {
        return try {
            val snapshot = db.collection("locations").get().await()
            // Lấy trường "name" từ mỗi document
            snapshot.documents.mapNotNull { it.getString("name") }
        } catch (e: Exception) {
            Log.e("FirestoreService", "Lỗi lấy địa điểm: ${e.message}")
            // Trả về danh sách mặc định nếu lỗi mạng
            listOf("TP. Hồ Chí Minh", "An Giang", "Kiên Giang", "Cần Thơ")
        }
    }
}

