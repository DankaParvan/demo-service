package com.itmo.microservices.demo.order.api.dto

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor
import java.util.*

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CatalogItem {
    private val uuid: UUID? = null
    private val title: String? = null
    private val description: String? = null
    private val price = 0
    private val amount = 0
}