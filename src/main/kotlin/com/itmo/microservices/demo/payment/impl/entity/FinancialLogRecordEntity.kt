package com.itmo.microservices.demo.payment.impl.entity

import com.itmo.microservices.demo.payment.api.model.FinancialOperationType
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class FinancialLogRecordEntity {
    @Id
    lateinit var orderId: UUID
    lateinit var paymentTransactionId: UUID
    lateinit var type: FinancialOperationType
    var amount: Int = 0
    var timestamp: Long = 0

    constructor()

    constructor(
        orderId: UUID,
        paymentTransactionId: UUID,
        type: FinancialOperationType,
        amount: Int,
        timestamp: Long
    ) {
        this.amount = amount
        this.type = type
        this.orderId = orderId
        this.paymentTransactionId = paymentTransactionId
        this.timestamp = timestamp
    }
}