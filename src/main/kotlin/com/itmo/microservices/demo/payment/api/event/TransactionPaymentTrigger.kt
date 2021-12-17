package com.itmo.microservices.demo.payment.api.event

import com.google.common.eventbus.EventBus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TransactionPaymentTrigger {
    @Autowired
    lateinit var eventBus: EventBus

    fun onTransactionHandled(event: TransactionRequestedEvent) {
        eventBus.post(event)
    }
}