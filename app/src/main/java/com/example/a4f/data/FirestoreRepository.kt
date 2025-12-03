package com.example.a4f.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp

object FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    data class TripModel(
        val id: String,
        val startTime: String,
        val startStation: String,
        val endTime: String,
        val endStation: String,
        val distanceTime: String,
        val price: Int,
        val seatsAvailable: Int,
        val seatType: String
    )

    // Lấy danh sách chuyến từ Firestore (lọc/format client-side)
    suspend fun getTrips(source: String?, destination: String?, dateString: String?): List<TripModel> {
        return try {
            val snapshot = db.collection("trips").get().await()
            Log.d("FirestoreRepository", "fetched trips snapshot size=${snapshot.size()}, filtering by source=$source, destination=$destination, date=$dateString")
            val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            val dateFormatter = java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault())
            val list = mutableListOf<TripModel>()
            for (doc in snapshot.documents) {
                Log.d("FirestoreRepository", "doc=${doc.id} data=${doc.data}")
                val id = doc.id
                // NOTE: Data in Firestore has departureTime & arrivalTime REVERSED
                // departureTime is actually when the bus arrives, arrivalTime is when it departs
                val departureTs = doc.getTimestamp("departureTime")
                val arrivalTs = doc.getTimestamp("arrivalTime")
                
                // SWAP: use arrivalTs for startTime (actual departure) and departureTs for endTime (actual arrival)
                var startTime = arrivalTs?.toDate()?.let { sdf.format(it) } ?: (doc.getString("arrivalTime") ?: "")
                var endTime = departureTs?.toDate()?.let { sdf.format(it) } ?: (doc.getString("departureTime") ?: "")

                // Extract trip date from arrivalTime (actual departure time)
                val tripDate = arrivalTs?.toDate()?.let { dateFormatter.format(it) } ?: ""
                
                // Filter by date if provided
                if (!dateString.isNullOrBlank() && tripDate != dateString) {
                    continue
                }

                // Handle 'from' which may be DocumentReference or String
                val startStation = try {
                    val fromObj = doc.get("from")
                    when (fromObj) {
                        is DocumentReference -> {
                            try {
                                val refDoc = fromObj.get().await()
                                refDoc.getString("name") ?: refDoc.getString("title") ?: fromObj.path.substringAfterLast('/') ?: (source ?: "")
                            } catch (e: Exception) {
                                fromObj.path.substringAfterLast('/').ifBlank { source ?: "" }
                            }
                        }
                        is String -> fromObj
                        else -> source ?: ""
                    }
                } catch (e: Exception) {
                    source ?: ""
                }

                // Handle 'to' which may be DocumentReference or String
                val endStation = try {
                    val toObj = doc.get("to")
                    when (toObj) {
                        is DocumentReference -> {
                            try {
                                val refDoc = toObj.get().await()
                                refDoc.getString("name") ?: refDoc.getString("title") ?: toObj.path.substringAfterLast('/') ?: (destination ?: "")
                            } catch (e: Exception) {
                                toObj.path.substringAfterLast('/').ifBlank { destination ?: "" }
                            }
                        }
                        is String -> toObj
                        else -> destination ?: ""
                    }
                } catch (e: Exception) {
                    destination ?: ""
                }

                val distanceKm = doc.get("distanceKm")
                val distanceTime = if (distanceKm != null) "${distanceKm}km" else (doc.getString("distanceTime") ?: "")

                val availableSeats = (doc.get("availableSeats") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                val bookedSeats = (doc.get("bookedSeats") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                val seatsAvailable = (availableSeats.size - bookedSeats.size).coerceAtLeast(0)

                val price = try { getTripPrice(id) } catch (e: Exception) { 0 }

                val busTypePath = doc.getString("busType") ?: ""
                val seatType = if (busTypePath.contains("sleeper") || busTypePath.contains("sleeper_bus")) "Giường nằm" else "Limousine"

                // Filter by source & destination (matching HomeScreen selections)
                val matchesRoute = (source.isNullOrBlank() || startStation.contains(source, ignoreCase = true)) &&
                        (destination.isNullOrBlank() || endStation.contains(destination, ignoreCase = true))
                
                if (matchesRoute) {
                    Log.d("FirestoreRepository", "Added trip: id=$id start=$startTime end=$endTime from=$startStation to=$endStation date=$tripDate")
                    list.add(TripModel(id, startTime, startStation, endTime, endStation, distanceTime, price, seatsAvailable, seatType))
                } else {
                    Log.d("FirestoreRepository", "Filtered out trip: id=$id from=$startStation to=$endStation (requested: $source → $destination)")
                }
            }
            list
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "getTrips error: ${e.message}")
            emptyList()
        }
    }

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
        destination: String? = null,
        status: String = "confirmed"  // ← Thêm tham số status với giá trị mặc định
    ) {
        val tripDoc = db.collection("trips").document(tripId).get().await()
        val arrivalTime = tripDoc.getTimestamp("arrivalTime") ?: Timestamp.now()  // ngày khởi hành
        // 1. Cập nhật ghế đã đặt trong trip
        db.collection("trips").document(tripId)
            .update("bookedSeats", FieldValue.arrayUnion(*newSeats.toTypedArray()))
            .await()

        // 2. Tạo booking document với đầy đủ thông tin
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
        val bookingData = hashMapOf(
            "bookedAt" to FieldValue.serverTimestamp(),
            "tripDate" to arrivalTime, // <--- lưu ngày khởi hành
            "seatNumber" to newSeats,
            "totalPrice" to totalPrice,
            "status" to status,  // ← Sử dụng status truyền vào
            "tripId" to tripId,
            "userId" to userId
        )
        if (source != null) bookingData["source"] = source
        if (destination != null) bookingData["destination"] = destination

        // 3. Lưu booking
        db.collection("bookings").add(bookingData).await()
    }
}
