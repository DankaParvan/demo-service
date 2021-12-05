package com.itmo.microservices.demo.warehouse.impl.service

import com.itmo.microservices.demo.warehouse.impl.repository.WarehouseItemRepository
import com.itmo.microservices.demo.warehouse.impl.repository.CatalogItemRepository
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.warehouse.api.exception.ItemIsNotExistException
import com.itmo.microservices.demo.warehouse.api.exception.ItemQuantityException
import java.util.UUID
import com.itmo.microservices.demo.warehouse.api.model.ItemQuantityRequestDTO
import com.itmo.microservices.demo.warehouse.impl.entity.CatalogItem
import com.itmo.microservices.demo.warehouse.impl.entity.WarehouseItem
import com.itmo.microservices.demo.warehouse.impl.logging.WarehouseServiceNotableEvents
import org.springframework.stereotype.Service

@Service
class WarehouseService(
    private val warehouseRepository: WarehouseItemRepository,
    private val catalogRepository: CatalogItemRepository
) {
    @InjectEventLogger
    private val eventLogger: EventLogger? = null

    fun checkAllItems(items: List<ItemQuantityRequestDTO>) {
        for (item in items) {
            if (!catalogRepository.existsById(item.id)) {
                throw ItemIsNotExistException("Item with id:${item.id}, not found")
            }
        }
    }

    fun checkAllQuantity(items: List<ItemQuantityRequestDTO>) {
        for (item in items) {
            if (item.amount > getItemQuantity(item.id)) {
                throw ItemQuantityException("Item with id:${item.id}, not enough quantity")
            }
        }
    }

    fun checkAllBookQuantity(items: List<ItemQuantityRequestDTO>) {
        for (item in items) {
            if (item.amount > getItemBooked(item.id)) {
                throw ItemQuantityException("Item with id:${item.id}, to many unbook amount")
            }
        }
    }

    private fun getItemQuantity(id: UUID): Int {
        val item = warehouseRepository.findWarehouseItemById(id)
        return item!!.amount!! - item.booked!!;
    }

    private fun getItemBooked(id: UUID): Int {
        val item = warehouseRepository.findWarehouseItemById(id)
        return item!!.booked!!
    }

    fun income(value: ItemQuantityRequestDTO) {
        val item = warehouseRepository.findWarehouseItemById(value.id) ?: throw ItemIsNotExistException("Item with id:${value.id}, not found")
        item.amount = item.amount?.plus(value.amount)
        eventLogger!!.info(WarehouseServiceNotableEvents.I_ITEM_QUANTITY_UPDATED, item.id)
        warehouseRepository.save(item)
    }

    fun outcome(value: ItemQuantityRequestDTO) {
        val item = warehouseRepository.findWarehouseItemById(value.id) ?: throw ItemIsNotExistException("Item with id:${value.id}, not found")
        item.amount = item.amount?.minus(value.amount)
        eventLogger!!.info(WarehouseServiceNotableEvents.I_ITEM_QUANTITY_UPDATED, item.id)
        warehouseRepository.save(item)
    }

    fun book(value: ItemQuantityRequestDTO) {
        val item = warehouseRepository.findWarehouseItemById(value.id) ?: throw ItemIsNotExistException("Item with id:${value.id}, not found")
        item.booked = item.booked?.plus(value.amount)
        eventLogger!!.info(WarehouseServiceNotableEvents.I_ITEM_QUANTITY_UPDATED, item.id)
        warehouseRepository.save(item)
    }

    fun unbook(value: ItemQuantityRequestDTO) {
        val item = warehouseRepository.findWarehouseItemById(value.id) ?: throw ItemIsNotExistException("Item with id:${value.id}, not found")
        item.booked = item!!.booked?.minus(value.amount)
        eventLogger!!.info(WarehouseServiceNotableEvents.I_ITEM_QUANTITY_UPDATED, item.id)
        warehouseRepository.save(item!!)
    }

    fun addItem(item: CatalogItem) {
        val catalogItem = catalogRepository.save(item)
        val warehouseItem = WarehouseItem(catalogItem, 0, 0)
        eventLogger!!.info(WarehouseServiceNotableEvents.I_ITEM_CREATED, catalogItem.id)
        warehouseRepository.save(warehouseItem)
        return
    }

    fun getItems(): List<CatalogItem?> {
        return catalogRepository.findAll()
    }

    fun getItem(id: UUID?): CatalogItem {
        return catalogRepository.findCatalogItemById(id) ?: throw ItemIsNotExistException("Item with id:${id}, not found")
    }

    fun getItemQuantity(id: UUID?): WarehouseItem {
        return warehouseRepository.findWarehouseItemById(id) ?: throw ItemIsNotExistException("Item with id:${id}, not found")
    }
}