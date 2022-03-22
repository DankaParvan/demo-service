package com.itmo.microservices.demo.order.impl.service

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.itmo.microservices.demo.order.api.service.IOrderService
import com.itmo.microservices.demo.payment.api.event.TransactionRequestedEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class PaymentEventListener(
    val orderService: IOrderService
) {
    @Autowired
    lateinit var eventBus: EventBus

    @PostConstruct
    fun init() {
        eventBus.register(this)
    }

    @Subscribe
    fun onReceivePayment(event: TransactionRequestedEvent) {
        println(event.message)
    }
}