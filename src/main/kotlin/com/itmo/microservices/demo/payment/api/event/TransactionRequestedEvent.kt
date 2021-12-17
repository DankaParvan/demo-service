package com.itmo.microservices.demo.payment.api.event

import com.itmo.microservices.commonlib.logging.NotableEvent
import com.itmo.microservices.demo.payment.api.model.PaymentStatus
import java.util.*

class TransactionRequestedEvent(
    val uuid: UUID,
    val status: PaymentStatus,
    val message: String?
) : NotableEvent {

    override fun getName() = name

    override fun getTemplate() = ""

    private companion object {
        const val name: String = "transaction_requested_event"
    }
}
