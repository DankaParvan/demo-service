package com.itmo.microservices.demo.order.impl.entity;

import com.itmo.microservices.demo.order.api.dto.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Entity
@Getter
@Setter
@ToString
@Table(name = "orders")
public class OrderEntity extends AbstractEntity {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private UUID uuid;

    @Column(name = "time_created")
    private LocalDateTime timeCreated;
    @Column(name = "status")
    private OrderStatus status = OrderStatus.COLLECTING;

    @Column(name = "delivery_info")
    private Timestamp deliveryInfo;
    @OneToMany
    @ToString.Exclude
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    public OrderEntity() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderEntity that = (OrderEntity) o;
        return uuid != null && Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public UUID getUuid() {
        return uuid;
    }
}
