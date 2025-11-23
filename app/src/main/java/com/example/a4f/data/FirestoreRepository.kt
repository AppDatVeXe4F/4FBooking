package com.example.a4f.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
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
                val bookedSeats = tripDoc.get("bookedSeats") as? List<String> ?: emptyList()
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

    // Hàm đặt vé (ĐÃ XÓA TODO)
    fun bookSeats(tripId: String, newSeats: List<String>, totalPrice: Int) {
        // 1. Cập nhật ghế vào chuyến xe
        db.collection("trips").document(tripId)
            .update("bookedSeats", FieldValue.arrayUnion(*newSeats.toTypedArray()))
            .addOnFailureListener { e -> Log.e("Firestore", "Lỗi update ghế: $e") }

        // 2. Lưu đơn hàng
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: "guest"

        val bookingData = hashMapOf(
            "bookedAt" to FieldValue.serverTimestamp(),
            "seatNumber" to newSeats,
            "totalPrice" to totalPrice,
            "status" to "confirmed",
            "tripId" to tripId,
            "userId" to userId
        )

        db.collection("bookings")
            .add(bookingData)
            .addOnSuccessListener { Log.d("Firestore", "Lưu đơn hàng thành công!") }
    }
}