package com.example.a4f.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TicketListViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    val tickets: StateFlow<List<Ticket>> = _tickets

    init {
        fetchTickets()
    }

    private fun fetchTickets() {
        viewModelScope.launch {
            try {
                val userId = "user_001" // TODO: user thật
                Log.d("TicketViewModel", "Fetching tickets for user: $userId")

                val snapshot = db.collection("bookings")
                    .whereEqualTo("user", db.collection("users").document(userId))
                    .orderBy("bookedAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                Log.d("TicketViewModel", "Total bookings fetched: ${snapshot.size()}")

                val list = snapshot.documents.map { doc ->

                    val bookedAt = doc.getTimestamp("bookedAt")
                    val seats = doc.get("seatNumber") as? List<String> ?: emptyList()
                    val status = doc.getString("status") ?: ""
                    val price = doc.getLong("totalPrice") ?: 0
                    val tripRef = doc.getDocumentReference("trip")
                    val userRef = doc.getDocumentReference("user")

                    // ✅ LẤY TRỰC TIẾP TỪ BOOKING
                    val source = doc.getString("source") ?: ""
                    val destination = doc.getString("destination") ?: ""

                    Log.d(
                        "TicketViewModel",
                        "Booking doc: id=${doc.id}, source=$source, destination=$destination"
                    )

                    Ticket(
                        id = doc.id,
                        bookedAt = bookedAt,
                        seatNumber = seats,
                        status = status,
                        totalPrice = price,
                        trip = tripRef,
                        user = userRef,
                        source = source,
                        destination = destination
                    )
                }

                _tickets.value = list

            } catch (e: Exception) {
                Log.e("TicketViewModel", "Lỗi khi fetch tickets: ${e.message}", e)
            }
        }
    }
}
