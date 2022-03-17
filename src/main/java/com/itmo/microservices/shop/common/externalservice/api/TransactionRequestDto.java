package com.itmo.microservices.shop.common.externalservice.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

public class TransactionRequestDto {
    private String clientSecret;

    public TransactionRequestDto(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public TransactionRequestDto() {

    }

    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionRequestDto that = (TransactionRequestDto) o;
        return Objects.equals(clientSecret, that.clientSecret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientSecret);
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    public String toString() {
        return "TransactionRequestDto{" +
                "clientSecret='" + clientSecret + '\'' +
                '}';
    }
}
