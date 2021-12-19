package com.itmo.microservices.demo.delivery.api.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SlotsModel(

    // TODO: 14.12.2021  
    var slotsDate: String?,
    var deliveryMen: MutableList<Int>?,
    var timeSlots: MutableList<String>?
)