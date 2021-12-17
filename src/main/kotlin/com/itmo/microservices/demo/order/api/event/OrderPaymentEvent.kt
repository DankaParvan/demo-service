package com.itmo.microservices.demo.order.api.event

import com.itmo.microservices.commonlib.logging.NotableEvent
import java.util.*

class OrderPaymentEvent(
    val orderId: UUID,
    val message: String?
) : NotableEvent {

    override fun getName() = name

    override fun getTemplate() = ""

    private companion object {
        const val name: String = "ORDER_PAYMENT_EVENT"
    }
}