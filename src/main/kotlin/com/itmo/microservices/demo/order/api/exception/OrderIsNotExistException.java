package com.itmo.microservices.demo.order.api.exception;

public class OrderIsNotExistException extends Exception {
    public OrderIsNotExistException(String message) {
        super(message);
    }
}
