package com.example.a4f.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

object FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    // --- HÀM MỚI: LẮNG NGHE DỮ LIỆU REAL-TIME ---
    // Hàm này sẽ tự động chạy mỗi khi Firebase có thay đổi
    fun listenToTripUpdates(
        tripId: String,
        onUpdate: (List<String>, Int) -> Unit // Callback trả về danh sách ghế và giá
    ): ListenerRegistration {
        val docRef = db.collection("trips").document(tripId)

        // addSnapshotListener: Là "cái ăng-ten" thu sóng từ Firebase
        return docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("Firestore", "Lỗi lắng nghe: ${e.message}")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // 1. Lấy danh sách ghế đã bán mới nhất
                val bookedSeats = snapshot.get("bookedSeats") as? List<String> ?: emptyList()

                // 2. Lấy giá vé (Logic nhảy bảng như cũ)
                val busTypePath = snapshot.getString("busType")
                var price = 0

                // Lưu ý: Việc lấy giá vé liên kết bảng trong Realtime hơi phức tạp nên ta sẽ xử lý riêng.
                // Ở đây tạm thời ta trả về dữ liệu ghế trước để tô màu đỏ ngay lập tức.
                // Để lấy giá vé realtime, ta cần 1 query riêng hoặc lưu giá trực tiếp vào trip.
                // Cách đơn giản nhất cho bài này: Lấy giá 1 lần, còn ghế thì lắng nghe liên tục.

                onUpdate(bookedSeats, 0) // Tạm thời trả về 0 giá, sẽ xử lý giá ở logic riêng
            }
        }
    }

    // Hàm lấy giá vé (Chạy 1 lần thôi vì giá ít khi đổi)
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

    // Hàm đặt vé - Chuyển thành suspend function để await
    suspend fun bookSeats(
        tripId: String, 
        newSeats: List<String>, 
        totalPrice: Int,
        source: String? = null,
        destination: String? = null
    ) {
        // 1. Cập nhật ghế đã đặt trong trip
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
        
        // Thêm source và destination nếu có
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