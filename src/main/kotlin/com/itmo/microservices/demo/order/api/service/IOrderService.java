package com.itmo.microservices.demo.order.api.service;

import com.itmo.microservices.demo.order.api.exception.BookingException;
import com.itmo.microservices.demo.order.api.dto.BookingDto;
import com.itmo.microservices.demo.order.api.dto.OrderDto;
import com.itmo.microservices.demo.order.api.exception.BookingException;
import com.itmo.microservices.demo.order.api.exception.OrderIsNotExistException;

import java.io.IOException;
import java.util.UUID;

public interface IOrderService {
    OrderDto createOrder();

    OrderDto getOrderById(UUID orderId) throws OrderIsNotExistException;

    OrderDto putItemToOrder(UUID orderId, UUID itemId, int amount);

    boolean startPayment(UUID orderId);

    BookingDto bookOrder(UUID orderId) throws BookingException, BookingException;

    BookingDto selectDeliveryTime(UUID orderId, int seconds) throws IOException;
}
