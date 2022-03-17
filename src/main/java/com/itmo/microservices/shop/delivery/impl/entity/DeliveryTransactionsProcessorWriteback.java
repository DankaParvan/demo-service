package com.itmo.microservices.shop.delivery.impl.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Entity
@ToString
public class DeliveryTransactionsProcessorWriteback {
    @Id
    private UUID id;
    private UUID orderId;
    private UUID userId;
    private int timeSlot;

    public DeliveryTransactionsProcessorWriteback() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryTransactionsProcessorWriteback that = (DeliveryTransactionsProcessorWriteback) o;
        return Objects.equals(id, that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public DeliveryTransactionsProcessorWriteback(UUID id, UUID orderId, UUID userId, int timeSlot) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.timeSlot = timeSlot;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(int timeSlot) {
        this.timeSlot = timeSlot;
    }
}
