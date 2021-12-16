package com.itmo.microservices.demo.internal.api.controller

import com.itmo.microservices.demo.internal.api.model.CatalogItemRequestDto
import com.itmo.microservices.demo.internal.api.model.DeliveryInfoRecord
import com.itmo.microservices.demo.internal.impl.service.InternalService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/_internal")
class InternalController(
    private val internalService: InternalService
) {
    @PostMapping("/catalogItem")
    fun addCatalogItem(@RequestBody request: CatalogItemRequestDto) =
        internalService.addCatalogItem(request)

    @GetMapping("/bookingHistory/{bookingId}")
    @Operation(security = [SecurityRequirement(name = "bearerAuth")])
    fun getBookingHistoryById(@PathVariable("bookingId") bookingId: UUID) =
        internalService.getBookingHistoryById(bookingId)

    @GetMapping("/deliveryLog/{orderId}")
    @Operation(security = [SecurityRequirement(name = "bearerAuth")])
    fun getDeliveryLog(@PathVariable("orderId") orderId: UUID): List<DeliveryInfoRecord> =
        internalService.getDeliveryLog(orderId)
}