package com.example.a4f.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.Date

data class Trip(
    val id: String,
    val fromName: String,
    val toName: String,
    val departureTime: Date?,
    val arrivalTime: Date?,
    val price: Int,
    val busType: String
)

object FirebaseService {
    private val db: FirebaseFirestore = Firebase.firestore

    // Lấy danh sách chuyến xe còn trống (khởi hành sau giờ hiện tại)
    suspend fun getActiveTrips(): List<Trip> {
        return try {
            val snapshot = db.collection("trips").get().await()
            snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null

                val fromRef = data["from"] as? DocumentReference ?: return@mapNotNull null
                val toRef = data["to"] as? DocumentReference ?: return@mapNotNull null

                val fromSnap = fromRef.get().await()
                val toSnap = toRef.get().await()

                val fromName = fromSnap.getString("name") ?: "Không rõ"
                val toName = toSnap.getString("name") ?: "Không rõ"

                Trip(
                    id = doc.id,
                    fromName = fromName,
                    toName = toName,
                    departureTime = (data["departureTime"] as? com.google.firebase.Timestamp)?.toDate(),
                    arrivalTime = (data["arrivalTime"] as? com.google.firebase.Timestamp)?.toDate(),
                    price = (data["basePrice"] as? Long)?.toInt() ?: 0,
                    busType = data["busType"] as? String ?: "unknown"
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}