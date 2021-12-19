package com.itmo.microservices.demo.internal.api.model

data class CatalogItemRequestDto(
    val title: String,
    val description: String,
    val price: Int,
    val amount: Int
)