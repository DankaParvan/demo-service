package com.itmo.microservices.demo.payment.impl.entity

import com.itmo.microservices.demo.payment.api.model.PaymentStatus
import com.itmo.microservices.demo.users.api.model.Status
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Payment(
    @Id
    var id: String,
    var status: PaymentStatus,
    var orderId: String,
) {
    constructor() : this(UUID.randomUUID().toString(), PaymentStatus.SUCCESS, "")

    constructor(status: PaymentStatus, orderId: UUID) : this(UUID.randomUUID().toString(), status, orderId.toString())
}