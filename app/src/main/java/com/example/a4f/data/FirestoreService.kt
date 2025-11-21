package com.example.a4f.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreService {
    private val db = FirebaseFirestore.getInstance()

    // ... (Hàm getLocationNames cũ giữ nguyên) ...

    suspend fun getLocationNames(): List<String> {
        // ... (Code cũ giữ nguyên) ...
        return try {
            val snapshot = db.collection("locations").get().await()
            snapshot.documents.mapNotNull { it.getString("name") }
        } catch (e: Exception) {
            listOf("TP. Hồ Chí Minh", "An Giang")
        }
    }

    // --- HÀM MỚI: LẤY ĐỊA CHỈ CỤ THỂ TỪ TÊN ---
    suspend fun getAddressByName(locationName: String): String {
        return try {
            // Tìm trong collection "locations" xem document nào có "name" bằng với tên địa điểm
            val snapshot = db.collection("locations")
                .whereEqualTo("name", locationName)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                // Lấy trường "address" của kết quả đầu tiên tìm được
                val address = snapshot.documents[0].getString("address") ?: ""
                if (address.isNotEmpty()) "$locationName: $address" else locationName
            } else {
                locationName // Không tìm thấy thì trả về tên gốc
            }
        } catch (e: Exception) {
            locationName // Lỗi thì trả về tên gốc
        }
    }
}