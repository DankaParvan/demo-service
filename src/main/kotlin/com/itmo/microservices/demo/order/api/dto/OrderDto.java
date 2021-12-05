package com.itmo.microservices.demo.order.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
public class OrderDto extends AbstractDto {
    public UUID uuid;
    public LocalDateTime timeCreated;
    public List<OrderItemDto> orderItems;
    public OrderStatus status;
    public Timestamp deliveryInfo;

    public OrderDto(UUID uuid, LocalDateTime timeCreated, List<OrderItemDto> orderItems, OrderStatus status, Timestamp deliveryInfo) {
        this.uuid = uuid;
        this.timeCreated = timeCreated;
        this.orderItems = orderItems;
        this.status = status;
        this.deliveryInfo = deliveryInfo;
    }
}


