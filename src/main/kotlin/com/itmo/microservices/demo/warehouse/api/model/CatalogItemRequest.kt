package com.itmo.microservices.demo.warehouse.api.model

data class CatalogItemRequest(
    var title: String,
    var description: String,
    var price: Int
)