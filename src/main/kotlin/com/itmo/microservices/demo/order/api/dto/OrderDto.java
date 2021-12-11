package com.itmo.microservices.demo.order.api.dto;

import com.itmo.microservices.demo.payment.api.model.PaymentLogRecordDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
public class OrderDto extends AbstractDto {
    public UUID uuid;
    public Long timeCreated;
    public OrderStatus status;
    public Map<UUID, Integer> itemsMap;
    public Integer deliveryDuration;
    public List<PaymentLogRecordDto> paymentHistory;

    public OrderDto(UUID uuid,
                    Long timeCreated,
                    OrderStatus status,
                    Map<UUID, Integer> itemsMap,
                    Integer deliveryDuration,
                    List<PaymentLogRecordDto> paymentHistory) {
        this.uuid = uuid;
        this.timeCreated = timeCreated;
        this.status = status;
        this.itemsMap = itemsMap;
        this.deliveryDuration = deliveryDuration;
        this.paymentHistory = paymentHistory;
    }
}


