package com.itmo.microservices.demo.order.api.dto;

import com.itmo.microservices.demo.payment.api.model.PaymentLogRecordDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
public class OrderDto extends AbstractDto {
    public UUID id;
    public Long timeCreated;
    public OrderStatus status;
    public Map<UUID, Integer> itemsMap;
    public Optional<Integer> deliveryDuration;
    public List<PaymentLogRecordDto> paymentHistory;

    public OrderDto(
            UUID id,
            Long timeCreated,
            OrderStatus status,
            Map<UUID, Integer> itemsMap,
            Optional<Integer> deliveryDuration,
            List<PaymentLogRecordDto> paymentHistory
    ) {
        this.id = id;
        this.timeCreated = timeCreated;
        this.status = status;
        this.itemsMap = itemsMap;
        this.deliveryDuration = deliveryDuration;
        this.paymentHistory = paymentHistory;
    }
}


