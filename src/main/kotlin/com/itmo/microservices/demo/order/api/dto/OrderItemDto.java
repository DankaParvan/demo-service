package com.itmo.microservices.demo.order.api.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
public class OrderItemDto extends AbstractDto {
    public UUID uuid;
    public String title;
    public Integer price;

    public OrderItemDto(UUID uuid, String title, Integer price) {
        this.uuid = uuid;
        this.title = title;
        this.price = price;
    }
}
