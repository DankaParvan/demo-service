package com.itmo.microservices.demo.internal.api.model

import java.util.*

data class BookingLogRecord(
    val bookingId: UUID,
    val itemId: UUID,
    val status: BookingStatus,
    val amount: Int,
    val timestamp: Long
)
