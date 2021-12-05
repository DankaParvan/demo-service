package com.itmo.microservices.demo.payment.api.controller

import com.itmo.microservices.demo.order.api.dto.OrderDto
import com.itmo.microservices.demo.order.api.dto.OrderItemDto
import com.itmo.microservices.demo.order.impl.service.OrderService
import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto
import com.itmo.microservices.demo.payment.impl.service.PaymentServiceImpl
import com.itmo.microservices.demo.warehouse.impl.service.WarehouseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/orders")
class PaymentController(
    val paymentService: PaymentServiceImpl,
    val orderService: OrderService,
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
}