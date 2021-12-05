package com.itmo.microservices.demo.payment.api.service

import com.itmo.microservices.demo.order.api.dto.OrderDto
import com.itmo.microservices.demo.payment.api.model.PaymentModel
import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto
import org.springframework.stereotype.Service
import java.util.*

@Service
interface PaymentService {
    fun executePayment(order: OrderDto): PaymentSubmissionDto
    fun fetchFinancialRecords(orderId: UUID?): List<UserAccountFinancialLogRecordDto>
}