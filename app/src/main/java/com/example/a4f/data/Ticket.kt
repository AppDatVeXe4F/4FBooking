package com.example.a4f.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class Ticket(
    val id: String = "",
    val bookedAt: Timestamp? = null,
    val seatNumber: List<String> = emptyList(),
    val status: String = "",
    val totalPrice: Long = 0,
    val trip: DocumentReference? = null,
    val user: DocumentReference? = null,
    val source: String = "",
    val destination: String = "",
    val isPaid: Boolean = false // ✅ trạng thái thanh toán
)
