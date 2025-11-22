// FirestoreRepository.kt
package com.example.a4f.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    // Lấy thông tin chuyến xe
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
            Log.e("Firestore", "Lỗi getTripInfo: ${e.message}")
            Pair(emptyList(), 0)
        }
    }

    // Đặt vé (thêm booking mới)
    fun createBooking(
        tripId: String,
        userId: String,
        seats: List<String>,
        totalPrice: Long,
        sourceName: String,
        destinationName: String
    ) {
        val bookingData = hashMapOf(
            "bookedAt" to Timestamp.now(),
            "seatNumber" to seats,
            "status" to "confirmed",
            "totalPrice" to totalPrice,
            "trip" to db.collection("trips").document(tripId),
            "user" to db.collection("users").document(userId),
            "source" to sourceName,
            "destination" to destinationName
        )

        db.collection("bookings")
            .add(bookingData)
            .addOnSuccessListener { Log.d("Firestore", "Đặt vé thành công!") }
            .addOnFailureListener { e -> Log.e("Firestore", "Lỗi createBooking: $e") }
    }

    // Cập nhật tuyến cho booking đã có
    fun updateBookingRoute(bookingId: String, sourceName: String, destinationName: String) {
        db.collection("bookings").document(bookingId)
            .update(
                mapOf(
                    "source" to sourceName,
                    "destination" to destinationName
                )
            )
            .addOnSuccessListener { Log.d("Firestore", "Cập nhật tuyến thành công") }
            .addOnFailureListener { e -> Log.e("Firestore", "Lỗi khi cập nhật tuyến: $e") }
    }

    // Đặt vé: cập nhật danh sách ghế của chuyến
    fun bookSeats(tripId: String, newSeats: List<String>) {
        db.collection("trips").document(tripId)
            .update("bookedSeats", FieldValue.arrayUnion(*newSeats.toTypedArray()))
            .addOnSuccessListener { Log.d("Firestore", "Cập nhật ghế thành công!") }
            .addOnFailureListener { e -> Log.e("Firestore", "Lỗi bookSeats: $e") }
    }
}
