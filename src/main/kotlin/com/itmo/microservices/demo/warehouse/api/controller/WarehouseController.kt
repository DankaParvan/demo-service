package com.itmo.microservices.demo.warehouse.api.controller

import com.itmo.microservices.demo.warehouse.api.exception.ItemIsNotExistException
import com.itmo.microservices.demo.warehouse.api.exception.ItemQuantityException
import com.itmo.microservices.demo.warehouse.api.model.CatalogItemDto
import com.itmo.microservices.demo.warehouse.impl.service.WarehouseService
import com.itmo.microservices.demo.warehouse.api.model.ItemQuantityRequestDTO
import com.itmo.microservices.demo.warehouse.api.model.ItemResponseDTO
import com.itmo.microservices.demo.warehouse.api.model.WarehouseItemDTO
import org.springframework.http.ResponseEntity
import com.itmo.microservices.demo.warehouse.impl.entity.CatalogItem
import com.itmo.microservices.demo.warehouse.impl.entity.WarehouseItem
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid
import kotlin.collections.ArrayList

@Validated
@RestController
@RequestMapping("/items")
@Transactional
class WarehouseController(private val service: WarehouseService) {

    @PutMapping(
        value = ["/income"],
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    @Operation(
        summary = "Execute a request to income items",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun income(@Valid @RequestBody items: List<@Valid ItemQuantityRequestDTO>): ResponseEntity<ItemResponseDTO> {
        try {
            service.checkAllItems(items)

            for (item in items) {
                service.income(item)
            }
        }
        catch (e: ItemIsNotExistException) {
            return ResponseEntity(ItemResponseDTO(404, e.message!!), HttpStatus.NOT_FOUND)
        }
        catch (e: Exception) {
            return ResponseEntity(ItemResponseDTO(400, e.message!!), HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(ItemResponseDTO(200, "Request executed successfully"), HttpStatus.OK)
    }

    @PutMapping(
        value = ["/outcome"],
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    @Operation(
        summary = "Execute a request to outcome items",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun outcome(@Valid @RequestBody items: List<@Valid ItemQuantityRequestDTO>): ResponseEntity<ItemResponseDTO> {
        try {
            service.checkAllItems(items)
            service.checkAllQuantity(items)

            for (item in items) {
                service.outcome(item)
            }
        }
        catch (e: ItemQuantityException) {
            return ResponseEntity(ItemResponseDTO(400, e.message!!), HttpStatus.NOT_FOUND)
        }
        catch (e: ItemIsNotExistException) {
            return ResponseEntity(ItemResponseDTO(404, e.message!!), HttpStatus.NOT_FOUND)
        }
        catch (e: Exception) {
            return ResponseEntity(ItemResponseDTO(400, e.message!!), HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(ItemResponseDTO(200, "Request executed successfully"), HttpStatus.OK)
    }

    @PostMapping(
        value = ["/book"],
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    @Operation(
        summary = "Execute a request to book items",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun book(@Valid @RequestBody items: List<@Valid ItemQuantityRequestDTO>): ResponseEntity<ItemResponseDTO> {
        try {
            service.checkAllItems(items)
            service.checkAllQuantity(items)

            for (item in items) {
                service.book(item)
            }
        }
        catch (e: ItemQuantityException) {
            return ResponseEntity(ItemResponseDTO(400, e.message!!), HttpStatus.NOT_FOUND)
        }
        catch (e: ItemIsNotExistException) {
            return ResponseEntity(ItemResponseDTO(404, e.message!!), HttpStatus.NOT_FOUND)
        }
        catch (e: Exception) {
            return ResponseEntity(ItemResponseDTO(400, e.message!!), HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(ItemResponseDTO(200, "Request executed successfully"), HttpStatus.OK)
    }

    @PutMapping(
        value = ["/unbook"],
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    @Operation(
        summary = "Execute a request to unbook items",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun unbook(@Valid @RequestBody items: List<@Valid ItemQuantityRequestDTO>): ResponseEntity<ItemResponseDTO> {
        try {
            service.checkAllItems(items)
            service.checkAllBookQuantity(items)

            for (item in items) {
                service.unbook(item)
            }
        }
        catch (e: ItemQuantityException) {
            return ResponseEntity(ItemResponseDTO(400, e.message!!), HttpStatus.NOT_FOUND)
        }
        catch (e: ItemIsNotExistException) {
            return ResponseEntity(ItemResponseDTO(404, e.message!!), HttpStatus.NOT_FOUND)
        }
        catch (e: Exception) {
            return ResponseEntity(ItemResponseDTO(400, e.message!!), HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(ItemResponseDTO(200, "Request executed successfully"), HttpStatus.OK)
    }

    @PostMapping(
        value = ["/add"],
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    @Operation(
        summary = "Execute a request to add new item",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun addItem(@Valid @RequestBody item: CatalogItem): ResponseEntity<ItemResponseDTO> {
        try {
            service.addItem(item)
        }
        catch (e: Exception) {
            return ResponseEntity(ItemResponseDTO(400, e.message!!), HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(ItemResponseDTO(200, item.id.toString()), HttpStatus.OK)
    }

    @GetMapping
    @Operation(
        summary = "Get a list of all added items",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getItems(@RequestParam("available") available: Boolean?): ResponseEntity<List<CatalogItemDto?>> {
        val list: ArrayList<CatalogItemDto?> = ArrayList()
        try {
            for (item in service.getItems()){

                if (item != null) {
                    list.add(item.toDto())
                }
            }
        }
        catch (e: Exception) {
            return ResponseEntity(null, HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(list.toList(), HttpStatus.OK)
    }

    @GetMapping("/get")
    @Operation(
        summary = "Get a item by id",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getItem(@RequestParam("id") id: UUID?): ResponseEntity<CatalogItemDto> {
        val item: CatalogItemDto
        try {
            item = service.getItem(id).toDto()
        }
        catch (e: ItemIsNotExistException) {
            return ResponseEntity(null, HttpStatus.NOT_FOUND)
        }
        catch (e: Exception) {
            return ResponseEntity(null, HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(item, HttpStatus.OK)
    }

    @GetMapping(value = ["/quantity"])
    @Operation(
        summary = "Get a item quantity by id",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getItemQuantity(@RequestParam("id") id: UUID?): ResponseEntity<WarehouseItemDTO> {
        val itemModel: WarehouseItemDTO
        try {
            val item: WarehouseItem = service.getItemQuantity(id)
            itemModel = WarehouseItemDTO(id!!, item.amount!!, item.booked!!)
        }
        catch (e: ItemIsNotExistException) {
            return ResponseEntity(null, HttpStatus.NOT_FOUND)
        }
        catch (e: Exception) {
            return ResponseEntity(null, HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(itemModel, HttpStatus.OK)
    }


    @GetMapping(value = ["/get/quantity"])
    @Operation(
        summary = "Get a items quantity by array of id",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getItemsQuantity(@RequestParam("id") id: List<UUID>?): ResponseEntity<List<WarehouseItemDTO>> {
        val itemModels: ArrayList<WarehouseItemDTO> = ArrayList()
        try {
            for (model_id in id!!){
                val item: WarehouseItem = service.getItemQuantity(model_id)
                itemModels.add(WarehouseItemDTO(model_id, item.amount!!, item.booked!!))
            }
        }
        catch (e: ItemIsNotExistException) {
            return ResponseEntity(null, HttpStatus.NOT_FOUND)
        }
        catch (e: Exception) {
            return ResponseEntity(null, HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(itemModels, HttpStatus.OK)
    }
    
    @PostMapping(value = ["/compare"])
    @Operation(
        summary = "Compare a items quantity by array of id",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun compareItemsQuantity(@Valid @RequestBody objects: List<ItemQuantityRequestDTO>): ResponseEntity<List<ItemQuantityRequestDTO>> {
        val itemModels: ArrayList<ItemQuantityRequestDTO> = ArrayList()
        try {
            for (obj in objects){
                val item: WarehouseItem = service.getItemQuantity(obj.id)
                itemModels.add(ItemQuantityRequestDTO(obj.id, item.amount!! - item.booked!! - obj.amount));
            }
        }
        catch (e: ItemIsNotExistException) {
            return ResponseEntity( ArrayList(), HttpStatus.NOT_FOUND)
        }
        catch (e: Exception) {
            return ResponseEntity( ArrayList(), HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(itemModels, HttpStatus.OK)
    }
}
