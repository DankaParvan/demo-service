package com.itmo.microservices.demo.delivery.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.common.exception.AccessDeniedException
import com.itmo.microservices.demo.delivery.api.messaging.DeliveryCreatedEvent
import com.itmo.microservices.demo.delivery.api.messaging.DeliveryDeletedEvent
import com.itmo.microservices.demo.delivery.api.messaging.SlotsCreatedEvent
import com.itmo.microservices.demo.delivery.api.model.DeliveryDTO
import com.itmo.microservices.demo.delivery.api.model.DeliveryModel
import com.itmo.microservices.demo.delivery.api.model.SlotsModel
import com.itmo.microservices.demo.delivery.api.service.DeliveryService
import com.itmo.microservices.demo.delivery.impl.entity.Delivery
import com.itmo.microservices.demo.delivery.impl.entity.Slots
import com.itmo.microservices.demo.delivery.impl.exeptions.BadRequestExeption
import com.itmo.microservices.demo.delivery.impl.exeptions.OutOfRangeException
import com.itmo.microservices.demo.delivery.impl.logging.DeliveryServiceNotableEvents
import com.itmo.microservices.demo.delivery.impl.repository.DeliveryRepository
import com.itmo.microservices.demo.delivery.impl.repository.SlotsRepository
import com.itmo.microservices.demo.delivery.impl.util.toModel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Suppress("UnstableApiUsage")
@Service
class DefaultDeliveryService(private val deliveryRepository: DeliveryRepository,
                             private val slotsRepository: SlotsRepository,
                             private val eventBus: EventBus): DeliveryService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun doDelivery(request: DeliveryDTO, user: UserDetails) {
        if (getSlot( request.preferredDeliveryTime) && request.preferredDeliveryTime >
                LocalDateTime.now().plusDays(1).withHour(0).withMinute(0)) {
            val deliveryEntity = request.toEntity(user)
            deliveryRepository.save(deliveryEntity)
            eventBus.post(DeliveryCreatedEvent(deliveryEntity.toModel()))
            eventLogger.info(DeliveryServiceNotableEvents.I_DELIVERY_CREATED, deliveryEntity.id)
        } else
            throw OutOfRangeException("Please, choose another delivery time")
    }

    override fun getDeliveryInfo(deliveryId: UUID, user: UserDetails): DeliveryModel {
        val delivery = deliveryRepository.findByIdOrNull(deliveryId)?.toModel()?:
            throw NotFoundException("Delivery with id : $deliveryId not found")
        if (delivery.user != user.username)
            throw AccessDeniedException("Cannot get delivery that was not created by you")
        return delivery
    }

    override fun setDeliverySlots(slots: Slots) {
        val dateFormatRegex = """(\d{4})-(\d{2})-(\d{2})""".toRegex()

        if (slots.deliveryMen?.size != slots.timeSlots?.size?.minus(1)) {
            throw BadRequestExeption("Delivery men count and count of time slots doesn't match")
        } else if (!slots.slotsDate.matches(dateFormatRegex)) {
            throw BadRequestExeption("Incorrect date format. Use 'YYYY-MM-DD'")
        }

        val startOfDay = LocalDateTime.parse(slots.slotsDate + "T00:00:00")
        val endOfDay = LocalDateTime.parse(slots.slotsDate + "T23:59:59")

        var outer = true
        val dayDeliveries = deliveryRepository.findAllByPreferredDeliveryTimeBetween(startOfDay, endOfDay)
        dayDeliveries.forEach{outer = getSlot(it.preferredDeliveryTime, slots, 1)}

        if (outer){
            if (slotsRepository.findByIdOrNull(slots.slotsDate) != null)
                slotsRepository.deleteById(slots.slotsDate)

            slotsRepository.save(slots)
            eventBus.post(SlotsCreatedEvent(slots.toModel()))
            eventLogger.info(DeliveryServiceNotableEvents.I_SLOTS_CREATED, slots.slotsDate)
        } else
            throw OutOfRangeException("Cannot set delivery slots. Some of the deliveries does not on the time slots")
    }

    override fun getDeliverySlots(date: Int): List<Int> {
//        val slots = slotsRepository.findByIdOrNull(date)?:
//            throw NotFoundException("Delivery slots not set")
//
//        val slotsMen = slots.deliveryMen
//        val slotsTime = slots.timeSlots
//
//        slotsMen?.forEachIndexed { index, element ->
//            if (slotsTime != null) {
//                slotsMen[index] = element -
//                        deliverySearch(date, slotsTime[index], slotsTime[index + 1])
//            }
//        }
//
//        val slotsAvailable = Slots(slots.slotsDate, slotsMen, slotsTime)

        val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 300)
        return list
    }

    override fun allDeliveries(user: UserDetails) = deliveryRepository.findAllByUser(user.username)
        .map { it.toModel() }

    override fun deleteDelivery(deliveryId: UUID, user: UserDetails) {
        val delivery = deliveryRepository.findByIdOrNull(deliveryId)?.toModel()?:
        throw NotFoundException("Delivery with id : $deliveryId not found")
        if (delivery.user != user.username)
            throw AccessDeniedException("Cannot delete delivery that was not created by you")
        runCatching {
            deliveryRepository.deleteById(deliveryId)
        }.onSuccess {
            eventBus.post(DeliveryDeletedEvent(deliveryId))
            eventLogger.info(DeliveryServiceNotableEvents.I_DELIVERY_DELETED, deliveryId)
        }.onFailure {
            throw NotFoundException("Delivery with id = $deliveryId not found", it)
        }
    }

    fun DeliveryDTO.toEntity(user: UserDetails): Delivery =
        Delivery(id = UUID.randomUUID(),
            user = user.username,
            type = this.type,
            warehouse = this.warehouse,
            preferredDeliveryTime = this.preferredDeliveryTime,
            address = this.address,
            courierCompany = "CDEC"
        )

    fun  deliverySearch (date: String, time1: String, time2: String) =
        deliveryRepository.findAllByPreferredDeliveryTimeBetween(LocalDateTime.parse(date + "T" + time1),
            LocalDateTime.parse(date + "T" + time2)).size


    fun getSlot(time: LocalDateTime?,
                slots: Slots? = slotsRepository.findByIdOrNull(time.toString().substring(0, 10)),
                lambda: Int = 0): Boolean {

        val date = time.toString().substring(0, 10)
        var answer = false

        val slotsMen = slots?.deliveryMen
        val slotsTime = slots?.timeSlots

        slotsMen?.forEachIndexed { index, element ->
            if (time != null && slotsTime != null) {
                if (time > LocalDateTime.parse(date + "T" + slotsTime[index]) &&
                    time < LocalDateTime.parse(date + "T" + slotsTime[index + 1])) {
                    answer = deliverySearch(date, slotsTime[index], slotsTime[index + 1]) < element + lambda
                }
            }
        }
        return answer
    }
}