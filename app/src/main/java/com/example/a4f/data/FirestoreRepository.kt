package com.example.a4f.data

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    // Hàm lấy thông tin chuyến xe
    suspend fun getTripInfo(tripId: String): Pair<List<String>, Int> {
        return try {
            val tripDoc = db.collection("trips").document(tripId).get().await()

            if (tripDoc.exists()) {
                // 1. Lấy danh sách ghế đã bán
                val bookedSeats = tripDoc.get("bookedSeats") as? List<String> ?: emptyList()

                // 2. Lấy đường dẫn loại xe để tìm giá
                val busTypePath = tripDoc.getString("busType")
                var price = 0

                if (!busTypePath.isNullOrEmpty()) {
                    val cleanPath = busTypePath.removePrefix("/")
                    val busTypeDoc = db.document(cleanPath).get().await()
                    if (busTypeDoc.exists()) {
                        price = busTypeDoc.getLong("price")?.toInt() ?: 0
                    }
                }
                Pair(bookedSeats, price)
            } else {
                Pair(emptyList(), 0)
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Lỗi: ${e.message}")
            Pair(emptyList(), 0)
        }
    }

    // Hàm đặt vé
    fun bookSeats(tripId: String, newSeats: List<String>) {
        db.collection("trips").document(tripId)
            .update("bookedSeats", FieldValue.arrayUnion(*newSeats.toTypedArray()))
            .addOnSuccessListener { Log.d("Firestore", "Đặt vé thành công!") }
            .addOnFailureListener { e -> Log.e("Firestore", "Lỗi đặt vé: $e") }
    }
}