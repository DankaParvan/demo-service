package com.itmo.microservices.demo.internal.impl.service

import com.itmo.microservices.demo.internal.api.model.*
import com.itmo.microservices.demo.internal.api.service.IInternalService
import com.itmo.microservices.demo.warehouse.api.model.CatalogItemDto
import com.itmo.microservices.demo.warehouse.impl.entity.CatalogItem
import com.itmo.microservices.demo.warehouse.impl.entity.WarehouseItem
import com.itmo.microservices.demo.warehouse.impl.repository.CatalogItemRepository
import com.itmo.microservices.demo.warehouse.impl.repository.WarehouseItemRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class InternalService(
    private val catalogItemRepository: CatalogItemRepository,
    private val warehouseItemRepository: WarehouseItemRepository
) : IInternalService {

    override fun addCatalogItem(catalogItemRequestDto: CatalogItemRequestDto): CatalogItemDto {
        val catalogItem = CatalogItem(
            title = catalogItemRequestDto.title,
            description = catalogItemRequestDto.description,
            price = catalogItemRequestDto.price
        )

        val catalogItemEntity = catalogItemRepository.save(catalogItem)
        val warehouseItem = WarehouseItem(catalogItemEntity, catalogItemRequestDto.amount, 0)

        warehouseItemRepository.save(warehouseItem)

        return CatalogItemDto(
            catalogItemEntity.id!!,
            catalogItemEntity.title!!,
            catalogItemEntity.description!!,
            catalogItemEntity.price,
            warehouseItem.amount!!
        )
    }

    override fun getBookingHistoryById(id: UUID): List<BookingLogRecord> {
        val itemsList = warehouseItemRepository.findWarehouseItemByIdAndBookedNotLike(id, 0)
        val bookingLogRecordList = itemsList.map {
            BookingLogRecord(
                it.id!!,
                it.item!!.id!!,
                BookingStatus.SUCCESS,
                it.amount!!,
                System.currentTimeMillis()
            )
        }
        return bookingLogRecordList
    }

    override fun getDeliveryLog(orderId: UUID): List<DeliveryInfoRecord> {
        return listOf(
            DeliveryInfoRecord(
                DeliverySubmissionOutcome.SUCCESS,
                System.currentTimeMillis(),
                1,
                System.currentTimeMillis() - 1500,
                UUID.randomUUID(),
                System.currentTimeMillis() - 3000
            ),
            DeliveryInfoRecord(
                DeliverySubmissionOutcome.SUCCESS,
                System.currentTimeMillis(),
                4,
                System.currentTimeMillis() - 2000,
                UUID.randomUUID(),
                System.currentTimeMillis() - 2500
            ),
            DeliveryInfoRecord(
                DeliverySubmissionOutcome.SUCCESS,
                System.currentTimeMillis(),
                2,
                System.currentTimeMillis() - 1200,
                UUID.randomUUID(),
                System.currentTimeMillis() - 2000
            )
        )
    }
}