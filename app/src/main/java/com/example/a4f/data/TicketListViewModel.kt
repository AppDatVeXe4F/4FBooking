package com.example.a4f.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class TicketListViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    val tickets: StateFlow<List<Ticket>> = _tickets
    var selectedTabIndex: Int = 0  // không dùng mutableStateOf

    init {
        fetchTickets()
    }

    private fun fetchTickets() {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId == null) {
                    Log.e("TicketViewModel", "User chưa đăng nhập")
                    return@launch
                }

                val snapshot = db.collection("bookings")
                    .whereEqualTo("userId", userId)
                    .orderBy("bookedAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                Log.d("TicketViewModel", "Đã fetch ${snapshot.size()} vé cho userId=$userId")

                val today = truncateTime(Date())

                val list = snapshot.documents.map { doc ->
                    val bookedAtDate = doc.getTimestamp("bookedAt")?.toDate()
                    val seats = doc.get("seatNumber") as? List<String> ?: emptyList()
                    val statusFromDb = doc.getString("status") ?: ""
                    val price = doc.getLong("totalPrice") ?: 0
                    val source = doc.getString("source") ?: ""
                    val destination = doc.getString("destination") ?: ""
                    val isPaid = doc.getBoolean("isPaid") ?: false
                    val tripId = doc.getString("tripId") ?: ""

                    // Cập nhật status theo ngày
                    val status = when {
                        statusFromDb.lowercase() == "cancelled" -> "cancelled"
                        bookedAtDate == null -> statusFromDb
                        truncateTime(bookedAtDate).after(today) -> "upcoming"
                        truncateTime(bookedAtDate) == today -> "today"
                        else -> "completed"
                    }

                    Log.d("TicketViewModel", "Ticket ${doc.id}: status=$status, bookedAt=$bookedAtDate")

                    Ticket(
                        id = doc.id,
                        bookedAt = bookedAtDate?.let { com.google.firebase.Timestamp(it) },
                        seatNumber = seats,
                        status = status,
                        totalPrice = price,
                        trip = tripId,
                        user = userId,
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

    private fun truncateTime(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    fun markTicketPaid(ticketId: String) {
        viewModelScope.launch {
            try {
                db.collection("bookings").document(ticketId)
                    .update("isPaid", true)
                    .await()

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
