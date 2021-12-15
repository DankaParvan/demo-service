package com.itmo.microservices.demo.payment.api.model

import java.util.*

data class TransactionResponse(
    val id: UUID,
    val status: String,
    val submitTime: Long,
    val completedTime: Long,
    val cost: Int,
    val delta: Int
)