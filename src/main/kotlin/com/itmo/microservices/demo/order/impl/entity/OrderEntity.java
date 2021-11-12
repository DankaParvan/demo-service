package com.itmo.microservices.demo.order.impl.entity;

import com.itmo.microservices.demo.order.api.dto.OrderDto;
import com.itmo.microservices.demo.order.api.dto.OrderStatus;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity(name = "order_table")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class OrderEntity {
    @Id
    private UUID uuid;

    private LocalDateTime timeCreated;
    private OrderStatus status;

    private Timestamp deliveryInfo;
    @OneToMany
    @ToString.Exclude
    private List<OrderItemEntity> catalogItems;

    public OrderEntity(UUID uuid, LocalDateTime timeCreated, OrderStatus status, Timestamp deliveryInfo, List<OrderItemEntity> catalogItems) {
        this.uuid = uuid;
        this.timeCreated = timeCreated;
        this.status = status;
        this.deliveryInfo = deliveryInfo;
        this.catalogItems = catalogItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderEntity order = (OrderEntity) o;
        return uuid != null && Objects.equals(uuid, order.uuid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public OrderDto toModel() {
        return new OrderDto(this.uuid, this.timeCreated, this.catalogItems.stream().map(OrderItemEntity::toModel).collect(Collectors.toList()), this.status, this.deliveryInfo);
    }
}