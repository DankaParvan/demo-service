package com.itmo.microservices.shop.order.impl.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
public class OrderTable {

  @Id
  @GeneratedValue
  private UUID id;
  private Long timeCreated;
  private Integer deliveryDuration;
  private Integer deliverySlot;
  private UUID userId;
  private UUID lastBookingId;

  @ManyToOne(fetch = FetchType.EAGER)
  private OrderStatus status;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "orderId")
  private Set<OrderItem> orderItems;

  public OrderTable() {

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    OrderTable orderTable = (OrderTable) o;
    return Objects.equals(id, orderTable.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    /* Using vanilla Java instead of lombok because of LAZY fetch type */
    return "OrderTable(" +
        "id=" + id +
        ", timeCreated=" + timeCreated +
        ", deliveryDuration=" + deliveryDuration +
        ", userId=" + userId +
        ", status=" + (Hibernate.isInitialized(status) ? status : "<NOT_FETCHED>") +
        ", orderItems=" + (Hibernate.isInitialized(orderItems) ? orderItems : "<NOT_FETCHED>") +
        ")";
  }

  public OrderTable(UUID id, Long timeCreated, Integer deliveryDuration, Integer deliverySlot, UUID userId, UUID lastBookingId, OrderStatus status, Set<OrderItem> orderItems) {
    this.id = id;
    this.timeCreated = timeCreated;
    this.deliveryDuration = deliveryDuration;
    this.deliverySlot = deliverySlot;
    this.userId = userId;
    this.lastBookingId = lastBookingId;
    this.status = status;
    this.orderItems = orderItems;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Long getTimeCreated() {
    return timeCreated;
  }

  public void setTimeCreated(Long timeCreated) {
    this.timeCreated = timeCreated;
  }

  public Integer getDeliveryDuration() {
    return deliveryDuration;
  }

  public void setDeliveryDuration(Integer deliveryDuration) {
    this.deliveryDuration = deliveryDuration;
  }

  public Integer getDeliverySlot() {
    return deliverySlot;
  }

  public void setDeliverySlot(Integer deliverySlot) {
    this.deliverySlot = deliverySlot;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public UUID getLastBookingId() {
    return lastBookingId;
  }

  public void setLastBookingId(UUID lastBookingId) {
    this.lastBookingId = lastBookingId;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public Set<OrderItem> getOrderItems() {
    return orderItems;
  }

  public void setOrderItems(Set<OrderItem> orderItems) {
    this.orderItems = orderItems;
  }
}
