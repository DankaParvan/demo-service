package com.itmo.microservices.demo.payment.impl.service

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.itmo.microservices.demo.order.api.dto.OrderDto
import com.itmo.microservices.demo.order.api.event.OrderPaymentEvent
import com.itmo.microservices.demo.order.impl.service.OrderService
import com.itmo.microservices.demo.internal.api.service.IInternalService
import com.itmo.microservices.demo.payment.api.service.PaymentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class OrderPaymentEventListener(
    private val orderService: OrderService,
    private val paymentService: PaymentService
) {
    @Autowired
    lateinit var eventBus: EventBus

    @PostConstruct
    fun init() {
        eventBus.register(this)
    }

    @Subscribe
    fun onReceiveOrderPayment(event: OrderPaymentEvent) {
        val order = orderService.getOrderById(event.orderId)

        println(event.message)

        paymentService.executePayment(
            OrderDto(
                order.id,
                order.timeCreated,
                order.status,
                order.itemsMap,
                order.deliveryDuration,
                order.paymentHistory
            )
        )
    }
}