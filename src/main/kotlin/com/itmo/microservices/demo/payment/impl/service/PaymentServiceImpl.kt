package com.itmo.microservices.demo.payment.impl.service

import com.google.gson.Gson
import com.itmo.microservices.demo.common.metrics.MetricsCollector
import com.itmo.microservices.demo.order.api.dto.OrderDto
import com.itmo.microservices.demo.payment.api.event.TransactionPaymentTrigger
import com.itmo.microservices.demo.payment.api.event.TransactionRequestedEvent
import com.itmo.microservices.demo.payment.api.model.*
import com.itmo.microservices.demo.payment.api.service.PaymentService
import com.itmo.microservices.demo.payment.impl.entity.FinancialLogRecordEntity
import com.itmo.microservices.demo.payment.impl.entity.Payment
import com.itmo.microservices.demo.payment.impl.metrics.PaymentMetrics
import com.itmo.microservices.demo.payment.impl.repository.FinancialLogRecordRepository
import com.itmo.microservices.demo.payment.impl.repository.PaymentRepository
import com.itmo.microservices.demo.warehouse.impl.repository.CatalogItemRepository
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

@Service
class PaymentServiceImpl(
    private val paymentRepository: PaymentRepository,
    private val financialLogRecordRepository: FinancialLogRecordRepository,
    private val catalogItemRepository: CatalogItemRepository,
    private val transactionPaymentTrigger: TransactionPaymentTrigger,
    private val metricsCollector: MetricsCollector
) : PaymentService {

    init {
        metricsCollector.register(*PaymentMetrics.VALUES)
    }

    override fun executePayment(order: OrderDto): PaymentSubmissionDto {
        val timestamp = System.currentTimeMillis()
        val payment = Payment(PaymentStatus.SUCCESS, order.id)
        val transactionId = paymentRepository.save(payment).id

        val client = HttpClient.newBuilder().build()

        val request = HttpRequest.newBuilder()
            .uri(
                URI.create(
                    "http://77.234.215.138:30027/transactions"
                )
            )
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    "{\n" +
                            "  \"id\": \"50b53508-71a3-4b0b-946f-b0bf898fb4d8\",\n" +
                            "  \"name\": \"payment_first\",\n" +
                            "  \"answerMethod\": \"TRANSACTION\",\n" +
                            "  \"projectId\": \"f694719a-5530-4c3d-b07d-a66e2ad6fc90\",\n" +
                            "  \"callbackUrl\": \"\",\n" +
                            "  \"clientSecret\": \"5c40e398-e0a7-4337-9d75-66347121eb42\",\n" +
                            "  \"accountLimits\": {\n" +
                            "    \"id\": \"5c6197bb-54de-491a-95a5-654c17a14d0d\",\n" +
                            "    \"acceptTransactions\": true,\n" +
                            "    \"enableResponseTimeVariation\": true,\n" +
                            "    \"responseTimeLowerBound\": 0,\n" +
                            "    \"responseTimeUpperBound\": 3,\n" +
                            "    \"enableFailures\": true,\n" +
                            "    \"failureProbability\": 5,\n" +
                            "    \"enableRateLimits\": false,\n" +
                            "    \"requestsPerMinute\": 0,\n" +
                            "    \"parallelRequests\": 0,\n" +
                            "    \"enableServerErrors\": true,\n" +
                            "    \"serverErrorProbability\": 2\n" +
                            "  },\n" +
                            "  \"transactionCost\": 5\n" +
                            "}"
                )
            )
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        val assWeCan = Gson().fromJson(response.body(), TransactionResponse::class.java)

        var sum = 0
        order.itemsMap.forEach { orderItemDto ->
            catalogItemRepository.findCatalogItemById(orderItemDto.key)?.let {
                sum += it.price * orderItemDto.value
            }
        }

        metricsCollector.handle(PaymentMetrics.REVENUE, sum.toDouble())
        metricsCollector.handle(PaymentMetrics.EXTERNAL_SYSTEM_EXPENSE, sum.toDouble())

        financialLogRecordRepository.save(
            FinancialLogRecordEntity(
                order.id,
                assWeCan.id,
                FinancialOperationType.WITHDRAW,
                sum,
                assWeCan.completedTime
            )
        )

        transactionPaymentTrigger.onTransactionHandled(
            TransactionRequestedEvent(
                assWeCan.id,
                PaymentStatus.valueOf(assWeCan.status),
                "Inform order about finishing payment"
            )
        )

        return PaymentSubmissionDto(
            assWeCan.completedTime,
            assWeCan.id
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

        return financialRecordsDto
    }
}