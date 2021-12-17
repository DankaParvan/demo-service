package com.itmo.microservices.demo.internal.api.service

import com.itmo.microservices.demo.delivery.impl.entity.Delivery
import com.itmo.microservices.demo.internal.api.model.BookingLogRecord
import com.itmo.microservices.demo.internal.api.model.CatalogItemRequestDto
import com.itmo.microservices.demo.internal.api.model.DeliveryInfoRecord
import com.itmo.microservices.demo.order.api.dto.OrderDto
import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto
import com.itmo.microservices.demo.warehouse.api.model.CatalogItemDto
import com.itmo.microservices.demo.warehouse.api.model.CatalogItemRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
interface IInternalService {
    fun addCatalogItem(catalogItemRequestDto: CatalogItemRequestDto): CatalogItemDto
    fun getBookingHistoryById(id: UUID): List<BookingLogRecord>
    fun getDeliveryLog(orderId: UUID): List<DeliveryInfoRecord>
}