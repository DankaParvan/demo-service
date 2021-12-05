package com.itmo.microservices.demo.payment.impl.service

import com.itmo.microservices.demo.order.api.dto.OrderDto
import com.itmo.microservices.demo.order.impl.dao.OrderRepository
import com.itmo.microservices.demo.order.impl.entity.OrderEntity
import com.itmo.microservices.demo.order.impl.service.OrderService
import com.itmo.microservices.demo.payment.api.model.*
import com.itmo.microservices.demo.payment.api.service.PaymentService
import com.itmo.microservices.demo.payment.impl.entity.FinancialLogRecordEntity
import com.itmo.microservices.demo.payment.impl.entity.Payment
import com.itmo.microservices.demo.payment.impl.repository.FinancialLogRecordRepository
import com.itmo.microservices.demo.payment.impl.repository.PaymentRepository
import com.itmo.microservices.demo.warehouse.impl.repository.CatalogItemRepository
import com.itmo.microservices.demo.warehouse.impl.repository.WarehouseItemRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PaymentServiceImpl(
    private val paymentRepository: PaymentRepository,
    private val financialLogRecordRepository: FinancialLogRecordRepository,
    private val catalogItemRepository: CatalogItemRepository,
    private val orderRepository: OrderRepository
    ): PaymentService {

    override fun executePayment(order: OrderDto): PaymentSubmissionDto {
        val timestamp = System.currentTimeMillis()
        val payment = Payment(PaymentStatus.SUCCESS, order.uuid)
        val transactionId = paymentRepository.save(payment).id

        var sum = 0
        order.orderItems.forEach { orderItemDto ->
            catalogItemRepository.findCatalogItemById(orderItemDto.catalogItemId)?.let {
                sum += it.price * orderItemDto.amount
            }
        }

        orderRepository.deleteById(order.uuid)

        financialLogRecordRepository.save(
            FinancialLogRecordEntity(
                order.uuid,
                UUID.fromString(transactionId),
                FinancialOperationType.WITHDRAW,
                sum,
                timestamp
            )
        )

        return PaymentSubmissionDto(
            timestamp,
            UUID.fromString(transactionId),
            sum
        )
    }

    override fun fetchFinancialRecords(orderId: UUID?): List<UserAccountFinancialLogRecordDto> {
        val financialRecords = if (orderId == null) {
            financialLogRecordRepository.findAll()
        } else {
            listOf(financialLogRecordRepository.getById(orderId))
        }

        val financialRecordsDto = financialRecords.map {
            UserAccountFinancialLogRecordDto(
                it.type,
                it.amount,
                it.orderId,
                it.paymentTransactionId,
                it.timestamp
            )
        }

        return  financialRecordsDto
    }
}