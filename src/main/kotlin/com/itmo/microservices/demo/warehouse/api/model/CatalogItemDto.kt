package com.itmo.microservices.demo.warehouse.api.model

import java.util.*
import javax.annotation.Nullable
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class CatalogItemDto(
    var id: UUID,
    @field:NotNull
    val title: String,
    @field:NotNull
    val description: String,
    @field:NotNull
    @field:Min(value = 1, message = "Price must be greater than 0")
    val price: Int,
    val amount: Int
)
