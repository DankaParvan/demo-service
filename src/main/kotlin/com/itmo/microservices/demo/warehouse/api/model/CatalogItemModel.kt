package com.itmo.microservices.demo.warehouse.api.model

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class CatalogItemModel(
    @field: NotNull
    val title: String,
    @field:NotNull
    val description: String,
    @field:NotNull
    @field:Min(value = 1, message = "Price must be greater than 0")
    val price: Int
)
