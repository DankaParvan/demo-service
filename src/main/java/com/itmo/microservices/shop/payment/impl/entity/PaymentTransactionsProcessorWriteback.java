package com.itmo.microservices.shop.payment.impl.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Entity
public class PaymentTransactionsProcessorWriteback {
    @Override
    public String toString() {
        return "PaymentTransactionsProcessorWriteback{" +
                "id=" + id +
                ", amount=" + amount +
                ", orderId=" + orderId +
                ", userId=" + userId +
                ", financialOperationTypeName='" + financialOperationTypeName + '\'' +
                '}';
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getFinancialOperationTypeName() {
        return financialOperationTypeName;
    }

    public void setFinancialOperationTypeName(String financialOperationTypeName) {
        this.financialOperationTypeName = financialOperationTypeName;
    }

    @Id
    private UUID id;
    private Integer amount;
    private UUID orderId;
    private UUID userId;
    private String financialOperationTypeName;

    public PaymentTransactionsProcessorWriteback() {
    }

    public PaymentTransactionsProcessorWriteback(Integer amount, UUID orderId, UUID userId, String financialOperationTypeName) {
        this.amount = amount;
        this.orderId = orderId;
        this.userId = userId;
        this.financialOperationTypeName = financialOperationTypeName;
    }

    public PaymentTransactionsProcessorWriteback(UUID id, Integer amount, UUID orderId, UUID userId, String financialOperationTypeName) {
        this.id = id;
        this.amount = amount;
        this.orderId = orderId;
        this.userId = userId;
        this.financialOperationTypeName = financialOperationTypeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        PaymentTransactionsProcessorWriteback paymentTransactionsProcessorWriteback = (PaymentTransactionsProcessorWriteback) o;
        return Objects.equals(id, paymentTransactionsProcessorWriteback.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
