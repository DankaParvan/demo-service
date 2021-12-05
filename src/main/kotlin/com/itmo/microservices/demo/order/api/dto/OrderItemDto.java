package com.itmo.microservices.demo.order.api.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
public class OrderItemDto extends AbstractDto {
    public UUID uuid;
    public UUID catalogItemId;
    public Integer amount;

    public OrderItemDto(UUID uuid, UUID catalogItemId, Integer amount) {
        this.uuid = uuid;
        this.catalogItemId = catalogItemId;
        this.amount = amount;
    }
}
