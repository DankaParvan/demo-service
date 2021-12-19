package com.itmo.microservices.demo.payment.api.controller

import com.google.gson.Gson
import com.itmo.microservices.demo.order.api.dto.OrderDto
import com.itmo.microservices.demo.order.impl.service.OrderService
import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payment.api.model.TransactionResponse
import com.itmo.microservices.demo.payment.impl.service.PaymentServiceImpl
import com.itmo.microservices.demo.users.api.model.UserRequestDto
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
@RequestMapping
class PaymentController(
    val paymentService: PaymentServiceImpl,
    val orderService: OrderService
) {

    @PostMapping("/orders/{orderId}/payment")
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
        @PathVariable orderId: UUID
    ): PaymentSubmissionDto {
        val order = orderService.getOrderById(orderId)

        return paymentService.executePayment(
            OrderDto(
                orderId,
                order.timeCreated,
                order.status,
                order.itemsMap,
                order.deliveryDuration,
                order.paymentHistory
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
        @RequestParam(name = "orderId", required = false) orderId: UUID?,
    ) = paymentService.fetchFinancialRecords(orderId)

    @PostMapping("/GACHI")
    @Operation(
        summary = "Ask external system for payment",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Authentication Error", responseCode = "401", content = [Content()]),
            ApiResponse(description = "Fuck you, Leatherman", responseCode = "429", content = [Content()]),
            ApiResponse(description = "Server go on on two blocks down", responseCode = "500", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun GACHI(): TransactionResponse {
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

        val ASSweCan = Gson().fromJson(response.body(), TransactionResponse::class.java)

        return ASSweCan
    }

    @GetMapping("/MUCHI/{transactionId}")
    fun MUCHI(
        @Parameter(hidden = false)
        @AuthenticationPrincipal
        @PathVariable transactionId: UUID,
    ): TransactionResponse {
        val client = HttpClient.newBuilder().build()

        val request = HttpRequest.newBuilder()
            .uri(
                URI.create(
                    "http://77.234.215.138:30027/transactions/$transactionId"
                )
            )
            .GET()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        val ASSweCan = Gson().fromJson(response.body(), TransactionResponse::class.java)

        return ASSweCan
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