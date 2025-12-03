package com.example.a4f.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreService {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getLocationNames(): List<String> {
        return try {
            val snapshot = db.collection("locations").get().await()
            snapshot.documents.mapNotNull { it.getString("name") }
        } catch (e: Exception) {
            listOf("TP. Hồ Chí Minh", "An Giang")
        }
    }

    // --- LẤY ĐỊA CHỈ CỤ THỂ TỪ TÊN ---
    suspend fun getAddressByName(locationName: String): String {
        return try {
            val snapshot = db.collection("locations")
                .whereEqualTo("name", locationName)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val address = snapshot.documents[0].getString("address") ?: ""
                if (address.isNotEmpty()) "$locationName: $address" else locationName
            } else {
                locationName
            }
        } catch (e: Exception) {
            locationName
        }
    }
}