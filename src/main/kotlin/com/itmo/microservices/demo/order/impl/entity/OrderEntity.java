package com.itmo.microservices.demo.order.impl.entity;

import com.itmo.microservices.demo.order.api.dto.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@Table(name = "orders")
public class OrderEntity extends AbstractEntity {
    @Id
    @GeneratedValue
    @Column(name = "uuid")
    private UUID id;

    @Column(name = "time_created")
    private Long timeCreated;
    @Column(name = "status")
    private OrderStatus status = OrderStatus.COLLECTING;

    @Column(name = "delivery_info")
    private Integer deliveryDuration;

    @ElementCollection
    @ToString.Exclude
    private Map<UUID, Integer> itemsMap = new HashMap<>();

    public OrderEntity() {
        this.timeCreated = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderEntity that = (OrderEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public UUID getUuid() {
        return id;
    }
}
