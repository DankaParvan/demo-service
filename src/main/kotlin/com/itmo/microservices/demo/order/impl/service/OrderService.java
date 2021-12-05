package com.itmo.microservices.demo.order.impl.service;

import com.itmo.microservices.demo.order.api.exception.BookingException;
import com.itmo.microservices.demo.order.api.dto.BookingDto;
import com.itmo.microservices.demo.order.api.dto.OrderDto;
import com.itmo.microservices.demo.order.api.dto.OrderStatus;
import com.itmo.microservices.demo.order.api.exception.OrderIsNotExistException;
import com.itmo.microservices.demo.order.api.service.IOrderService;
import com.itmo.microservices.demo.order.impl.dao.OrderItemRepository;
import com.itmo.microservices.demo.order.impl.dao.OrderRepository;
import com.itmo.microservices.demo.order.impl.entity.OrderEntity;
import com.itmo.microservices.demo.order.impl.entity.OrderItemEntity;
import com.itmo.microservices.demo.order.impl.external.PaymentApi;
import com.itmo.microservices.demo.order.impl.external.WarehouseApi;
import com.itmo.microservices.demo.order.util.mapping.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderDto createOrder() {
        OrderEntity newOrder = new OrderEntity();
        orderRepository.save(newOrder);
        return orderMapper.toDto(newOrder);
    }

    @Override
    public OrderDto getOrderById(UUID uuid) throws OrderIsNotExistException {
        var order = orderMapper.toDto(orderRepository.getById(uuid));
        if (order == null) throw new OrderIsNotExistException("Order is not exist");
        return order;
    }

    @Override
    public OrderDto putItemToOrder(UUID orderId, UUID itemId, int amount) {
        try {
            var order = orderRepository.getById(orderId);
            if (order.getStatus() == OrderStatus.BOOKED) {
                WarehouseApi warehouseApi = new WarehouseApi();
                warehouseApi.unbook(orderMapper.toDto(order));
                order.setStatus(OrderStatus.COLLECTING);
            }
            var orderItem = new OrderItemEntity(orderId, itemId, amount);

            order.getOrderItems().add(orderItem);

            orderRepository.save(order);
            orderItemRepository.save(orderItem);
            return orderMapper.toDto(order);
        } catch (javax.persistence.EntityNotFoundException e) {
            return null;
        }
    }

    @Override
    public BookingDto book(UUID orderId) {
        try {
            var order = orderRepository.getById(orderId);
            if (order.getStatus() != OrderStatus.COLLECTING) {
                return null;
            }
            // TODO: provide auth token
            WarehouseApi warehouseApi = new WarehouseApi();
            Set failed = warehouseApi.book(orderMapper.toDto(order)).getBody();

            if (failed == null) {
                throw new BookingException("No response from warehouse. Try again later");
            }
            if (failed.isEmpty()) {
                order.setStatus(OrderStatus.BOOKED);
            }

            return new BookingDto(orderId, failed);
        } catch (javax.persistence.EntityNotFoundException e) {
            return null;
        } catch (BookingException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public boolean startPayment(UUID orderId) {
        var order = orderRepository.getById(orderId);
        if (order.getStatus() != OrderStatus.BOOKED) {
            return false;
        }

        PaymentApi paymentApi = new PaymentApi();
        paymentApi.pay(orderMapper.toDto(order));

        return true;
    }

    @Override
    public BookingDto selectDeliveryTime(UUID orderId, int seconds) {
        try {
            var order = orderRepository.getById(orderId);
            if (order.getStatus() == OrderStatus.BOOKED) {
                order.setDeliveryInfo(new Timestamp(TimeUnit.SECONDS.toMillis(seconds)));
                orderRepository.save(order);
            }
        } catch (javax.persistence.EntityNotFoundException e) {
            return null;
        }
        return new BookingDto(orderId, new HashSet<>());
    }

    @Override
    public boolean finalizePayment(UUID orderId) {
        try {
            var order = orderRepository.getById(orderId);
            if (order.getStatus() != OrderStatus.BOOKED) {
                return false;
            }
            //todo: get result from PaymentService -> OK=PAID; Bad_Request=BOOKED
            order.setStatus(OrderStatus.PAID);
        } catch (javax.persistence.EntityNotFoundException e) {
            return false;
        }
        return true;
    }
}
