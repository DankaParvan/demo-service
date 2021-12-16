package com.itmo.microservices.demo.warehouse.impl.repository

import com.itmo.microservices.demo.warehouse.impl.entity.WarehouseItem
import org.springframework.data.jpa.repository.*
import java.util.*
import javax.persistence.LockModeType
import javax.persistence.QueryHint


interface WarehouseItemRepository : JpaRepository<WarehouseItem?, UUID?> {
    override fun existsById(uuid: UUID): Boolean

    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints(value = [QueryHint(name = "javax.persistence.lock.timeout", value = "1000")])
    fun findWarehouseItemById(uuid: UUID?): WarehouseItem?

    @Modifying
    @Query("update WarehouseItem set amount = :amount where id = :id")
    fun updateAmountById(id: UUID, amount: Int)

    fun findWarehouseItemByIdAndBookedNotLike(id: UUID, booked: Int): List<WarehouseItem>
}