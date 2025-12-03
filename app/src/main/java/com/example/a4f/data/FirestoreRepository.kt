package com.example.a4f.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

object FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    // --- DỮ LIỆU REAL-TIME ---
    fun listenToTripUpdates(
        tripId: String,
        onUpdate: (List<String>, Int) -> Unit
    ): ListenerRegistration {
        val docRef = db.collection("trips").document(tripId)
        return docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("Firestore", "Lỗi lắng nghe: ${e.message}")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // 1. Lấy danh sách ghế đã bán mới nhất
                val bookedSeats = snapshot.get("bookedSeats") as? List<String> ?: emptyList()

                // 2. Lấy giá vé
                val busTypePath = snapshot.getString("busType")
                var price = 0

                onUpdate(bookedSeats, 0)
            }
        }
    }

    // Hàm lấy giá vé
    suspend fun getTripPrice(tripId: String): Int {
        return try {
            val tripDoc = db.collection("trips").document(tripId).get().await()
            val busTypePath = tripDoc.getString("busType")
            if (!busTypePath.isNullOrEmpty()) {
                val cleanPath = busTypePath.removePrefix("/")
                val busTypeDoc = db.document(cleanPath).get().await()
                busTypeDoc.getLong("price")?.toInt() ?: 0
            } else 0
        } catch (e: Exception) { 0 }
    }

    suspend fun bookSeats(
        tripId: String, 
        newSeats: List<String>, 
        totalPrice: Int,
        source: String? = null,
        destination: String? = null
    ) {
        // 1. Ghế đã đặt trong trip
        db.collection("trips").document(tripId)
            .update("bookedSeats", FieldValue.arrayUnion(*newSeats.toTypedArray()))
            .await()

        // 2. Tạo booking document với đầy đủ thông tin
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
        val bookingData = hashMapOf(
            "bookedAt" to FieldValue.serverTimestamp(),
            "seatNumber" to newSeats,
            "totalPrice" to totalPrice,
            "status" to "confirmed",
            "tripId" to tripId,
            "userId" to userId
        )

        if (source != null) {
            bookingData["source"] = source
        }
        if (destination != null) {
            bookingData["destination"] = destination
        }
        
        // 3. Lưu booking và await để đảm bảo hoàn thành trước khi tiếp tục
        db.collection("bookings").add(bookingData).await()
    }
}