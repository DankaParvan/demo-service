package com.itmo.microservices.shop.common.externalservice.api;

import com.itmo.microservices.shop.common.transactions.TransactionStatus;
import com.itmo.microservices.shop.common.transactions.TransactionWrapper;
import lombok.Data;

import java.util.UUID;

public class TransactionResponseDto {
    private UUID id;
    private String status;
    private Long submitTime;

    public TransactionResponseDto(UUID id, String status, Long submitTime, Long completedTime, Integer cost, Integer delta) {
        this.id = id;
        this.status = status;
        this.submitTime = submitTime;
        this.completedTime = completedTime;
        this.cost = cost;
        this.delta = delta;
    }

    public TransactionResponseDto() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Long submitTime) {
        this.submitTime = submitTime;
    }

    public Long getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(Long completedTime) {
        this.completedTime = completedTime;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getDelta() {
        return delta;
    }

    public void setDelta(Integer delta) {
        this.delta = delta;
    }

    private Long completedTime;
    private Integer cost;
    private Integer delta;

    public TransactionWrapper<TransactionResponseDto, UUID> toTransactionWrapper() {
        TransactionResponseDto thisRef = this;
        return new TransactionWrapper<>() {
            @Override
            public TransactionResponseDto getWrappedObject() {
                return thisRef;
            }

            @Override
            public UUID getId() {
                return thisRef.getId();
            }

            @Override
            public TransactionStatus getStatus() {
                switch (thisRef.getStatus()) {
                    case "PENDING":
                        return TransactionStatus.PENDING;
                    case "SUCCESS":
                        return TransactionStatus.SUCCESS;
                    case "FAILURE":
                        return TransactionStatus.FAILURE;
                    default:
                        throw new IllegalStateException();
                }
            }
        };
    }
}
