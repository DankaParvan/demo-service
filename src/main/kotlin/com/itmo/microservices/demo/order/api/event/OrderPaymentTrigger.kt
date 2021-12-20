package com.itmo.microservices.demo.order.api.event

import com.google.common.eventbus.EventBus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OrderPaymentTrigger {
    @Autowired
    lateinit var eventBus: EventBus

    fun onOrderPaymentHandled(event: OrderPaymentEvent) {
        eventBus.post(event)
    }
}