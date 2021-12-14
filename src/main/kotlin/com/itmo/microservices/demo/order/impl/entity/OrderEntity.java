package com.itmo.microservices.demo.order.impl.entity;

import com.itmo.microservices.demo.order.api.dto.OrderStatus;
import com.itmo.microservices.demo.payment.api.model.PaymentLogRecordDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;


@Entity
@Getter
@Setter
@ToString
@Table(name = "orders")
public class OrderEntity extends AbstractEntity {
    @Id
    @GeneratedValue
    @Column(name = "uuid")
    private UUID uuid;

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
