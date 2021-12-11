package com.itmo.microservices.demo.order.impl.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "order_item_entity")
public class OrderItemEntity extends AbstractEntity {
    @Id
    @GeneratedValue
    @Column(name = "uuid")
    private UUID uuid;
    @Column(name = "catalog_item_id")
    private UUID catalogItemId;
    @Column(name = "amount")
    private Integer amount;

    public OrderItemEntity(UUID catalogItemId, Integer amount) {
        this.catalogItemId = catalogItemId;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderItemEntity that = (OrderItemEntity) o;
        return uuid != null && Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}