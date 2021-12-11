package com.itmo.microservices.demo.payment.impl.service

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.itmo.microservices.demo.order.api.dto.OrderDto
import com.itmo.microservices.demo.order.api.event.OrderPaymentEvent
import com.itmo.microservices.demo.order.api.service.IOrderService
import com.itmo.microservices.demo.payment.api.service.PaymentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class OrderPaymentEventListener(
    private val orderService: IOrderService,
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
        val orderItems = order.orderItems

        println(event.message)

        paymentService.executePayment(
            OrderDto(
                event.orderId,
                order.timeCreated,
                orderItems,
                order.status,
                order.deliveryInfo
            )
        )
    }
}