package com.itmo.microservices.demo.payment.api.controller

import com.google.gson.Gson
import com.itmo.microservices.demo.order.api.dto.OrderDto
import com.itmo.microservices.demo.order.impl.service.OrderService
import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payment.api.model.TransactionResponse
import com.itmo.microservices.demo.payment.impl.service.PaymentServiceImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import kotlin.io.path.Path

@RestController
@RequestMapping("/orders")
class PaymentController(
    val paymentService: PaymentServiceImpl,
    val orderService: OrderService
) {

    @PostMapping("/{orderId}/payment")
    @Operation(
        summary = "Execute Payment",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Payment Failed", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun executePayment(
        @Parameter(hidden = false)
        @AuthenticationPrincipal
        @PathVariable orderId: UUID,
    ): PaymentSubmissionDto {
        val order = orderService.getOrderById(orderId)
        val orderItems = order.orderItems
        return paymentService.executePayment(
            OrderDto(
                orderId,
                order.timeCreated,
                orderItems,
                order.status,
                order.deliveryInfo
            )
        )
    }

    @GetMapping("/finlog")
    @Operation(
        summary = "Get financial log",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Retrieving finlog data failed", responseCode = "500", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun fetchFinancialRecords(
        @Parameter(hidden = false)
        @AuthenticationPrincipal
        @RequestParam(required = false) orderId: UUID?,
    ) = paymentService.fetchFinancialRecords(orderId)

    @GetMapping("/GACHI")
    fun GACHI() {
        val client = HttpClient.newBuilder().build()

        val request = HttpRequest.newBuilder()
            .uri(
                URI.create(
                    "http://77.234.215.138:30027/transactions"
                )
            )
            .POST(
                HttpRequest.BodyPublishers.ofString("{\n" +
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
                        "}")
            )
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        var ASSweCan = Gson().fromJson(response.body(), TransactionResponse::class.java)

        println(ASSweCan.cost)
    }

//    @GetMapping("/some")
//    fun getSome() {
//        transactionPaymentTrigger.onTransactionHandled(
//            TransactionRequestedEvent(
//                UUID.randomUUID(),
//                PaymentStatus.SUCCESS,
//                "message"
//            )
//        )
//    }
}