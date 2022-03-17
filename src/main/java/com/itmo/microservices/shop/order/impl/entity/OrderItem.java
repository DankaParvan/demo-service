package com.itmo.microservices.shop.order.impl.entity;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

@Entity
@IdClass(OrderItemID.class)
public class OrderItem {
  public OrderItem(UUID orderId, UUID itemId, OrderTable order, Integer amount, Integer price) {
    this.orderId = orderId;
    this.itemId = itemId;
    this.order = order;
    this.amount = amount;
    this.price = price;
  }

  public OrderItem() {

  }

  public UUID getOrderId() {
    return orderId;
  }

  public void setOrderId(UUID orderId) {
    this.orderId = orderId;
  }

  public UUID getItemId() {
    return itemId;
  }

  public void setItemId(UUID itemId) {
    this.itemId = itemId;
  }

  public OrderTable getOrder() {
    return order;
  }

  public void setOrder(OrderTable order) {
    this.order = order;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public Integer getPrice() {
    return price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }

  @Id
  public UUID orderId;

  @Id
  public UUID itemId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "orderId",
      nullable = false,
      insertable = false,
      updatable = false)
  private OrderTable order;

  private Integer amount;

  private Integer price;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    OrderItem orderItem = (OrderItem) o;
    return Objects.equals(orderId, orderItem.orderId)
        && Objects.equals(itemId, orderItem.itemId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(orderId, itemId);
  }

  @Override
  public String toString() {
    /* Using vanilla Java instead of lombok because of LAZY fetch type */
    return "OrderItem(" +
        "orderId=" + orderId +
        ", itemId=" + itemId +
        ", order=" + (Hibernate.isInitialized(order) ? order : "<NOT_FETCHED>") +
        ", amount=" + amount +
        ", price=" + price +
        ")";
  }
}
