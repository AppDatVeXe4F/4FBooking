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
                val userId = "user_001" // TODO: thay bằng user thật
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
                    val source = doc.getString("source") ?: ""
                    val destination = doc.getString("destination") ?: ""
                    val isPaid = doc.getBoolean("isPaid") ?: false // ✅ lấy trạng thái thanh toán

                    Log.d(
                        "TicketViewModel",
                        "Booking doc: id=${doc.id}, source=$source, destination=$destination, isPaid=$isPaid"
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
                        destination = destination,
                        isPaid = isPaid
                    )
                }

                _tickets.value = list

            } catch (e: Exception) {
                Log.e("TicketViewModel", "Lỗi khi fetch tickets: ${e.message}", e)
            }
        }
    }

    // ✅ Hàm đánh dấu đã thanh toán
    fun markTicketPaid(ticketId: String) {
        viewModelScope.launch {
            try {
                db.collection("bookings").document(ticketId)
                    .update("isPaid", true)
                    .await()

                // Cập nhật local list luôn
                _tickets.value = _tickets.value.map { t ->
                    if (t.id == ticketId) t.copy(isPaid = true) else t
                }

                Log.d("TicketViewModel", "Ticket $ticketId marked as paid.")
            } catch (e: Exception) {
                Log.e("TicketViewModel", "Lỗi khi update thanh toán: ${e.message}", e)
            }
        }
    }
}
