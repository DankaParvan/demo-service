package com.itmo.microservices.demo.delivery.api.service

import com.itmo.microservices.demo.delivery.api.model.DeliveryDTO
import com.itmo.microservices.demo.delivery.api.model.DeliveryModel
import com.itmo.microservices.demo.delivery.api.model.SlotsModel
import com.itmo.microservices.demo.delivery.impl.entity.Slots
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDate
import java.util.*

interface DeliveryService {
    fun getDeliveryInfo(deliveryId: UUID, user: UserDetails): DeliveryModel?
    fun getDeliverySlots(date: Int): List<Int>
    fun setDeliverySlots(slots: Slots)
    fun doDelivery(request: DeliveryDTO, user: UserDetails)
    fun allDeliveries(user: UserDetails): List<DeliveryModel>
    fun deleteDelivery(deliveryId: UUID, user: UserDetails)
}