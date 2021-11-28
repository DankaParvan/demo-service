package com.itmo.microservices.demo.warehouse.impl.entity

import java.util.UUID
import com.fasterxml.jackson.annotation.JsonBackReference
import com.itmo.microservices.demo.warehouse.api.model.CatalogItemDto
import javax.persistence.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@Entity
@Table(name = "catalog_items")
class CatalogItem {
    @Id
    @GeneratedValue
    var id: UUID? = null

    @JsonBackReference
    @OneToOne(cascade = [CascadeType.REMOVE], fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    var warehouseItem: WarehouseItem? = null

    @field:NotNull(message = "Title must be not null")
    var title: String? = null

    @field:NotNull(message = "Description must be not null")
    var description: String? = null

    @field:NotNull(message = "Price must be not null")
    @field:Min(value = 1, message = "Price must be greater than 0")
    var price = 100

    constructor() {
    }

    constructor(
        id: UUID?,
        warehouseItem: WarehouseItem?,
        title: String?,
        description: String?,
        price: Int
    ) {
        this.id = id
        this.warehouseItem = warehouseItem
        this.title = title
        this.description = description
        this.price = price
    }

    constructor(title: String?, description: String?, price: Int) {
        this.title = title
        this.description = description
        this.price = price
    }

    fun toDto(): CatalogItemDto {
        val dto: CatalogItemDto = CatalogItemDto(
            id!!,
            title!!,
            description!!,
            price,
            warehouseItem?.amount!! - warehouseItem?.booked!!)

        return dto;
    }
}