package com.itmo.microservices.shop.payment.api.model;

import com.itmo.microservices.shop.payment.impl.entity.PaymentLogRecord;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

public class PaymentSubmissionDto {

    private Long timestamp;
    private UUID transactionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentSubmissionDto that = (PaymentSubmissionDto) o;
        return Objects.equals(timestamp, that.timestamp) && Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, transactionId);
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentSubmissionDto(Long timestamp, UUID transactionId) {
        this.timestamp = timestamp;
        this.transactionId = transactionId;
    }

    public PaymentSubmissionDto() {
    }

    public static PaymentSubmissionDto toModel(PaymentLogRecord paymentLogRecord) {
        PaymentSubmissionDto model = new PaymentSubmissionDto();
        model.setTimestamp(paymentLogRecord.getTimestamp());
        model.setTransactionId(paymentLogRecord.getTransactionId());
        model.setTimestamp(paymentLogRecord.getTimestamp());
        return model;
    }

}
