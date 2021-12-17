package com.itmo.microservices.demo.order.api.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
public class OrderItemDto extends AbstractDto {
    public UUID id;
    public String title;
    public Integer price;

    public OrderItemDto(UUID id, String title, Integer price) {
        this.id = id;
        this.title = title;
        this.price = price;
    }
}
