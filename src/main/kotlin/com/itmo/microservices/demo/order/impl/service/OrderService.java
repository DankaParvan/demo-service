package com.itmo.microservices.demo.order.impl.service;

import com.itmo.microservices.demo.order.api.BookingException;
import com.itmo.microservices.demo.order.api.dto.BookingDto;
import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.demo.warehouse.api.model.ItemResponseDTO;
import com.itmo.microservices.demo.order.api.dto.OrderDto;
import com.itmo.microservices.demo.order.api.dto.OrderStatus;
import com.itmo.microservices.demo.order.api.service.IOrderService;
import com.itmo.microservices.demo.order.impl.dao.OrderItemRepository;
import com.itmo.microservices.demo.order.impl.dao.OrderRepository;
import com.itmo.microservices.demo.order.impl.entity.BookingAttemptStatus;
import com.itmo.microservices.demo.order.impl.entity.BookingResponse;
import com.itmo.microservices.demo.order.impl.entity.OrderEntity;
import com.itmo.microservices.demo.order.impl.entity.OrderItemEntity;
import com.itmo.microservices.demo.order.util.mapping.OrderMapper;
import com.itmo.microservices.demo.payment.api.service.PaymentService;
import com.itmo.microservices.demo.warehouse.api.model.ItemQuantityRequestDTO;
import com.itmo.microservices.demo.warehouse.impl.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final WarehouseService warehouseService;
    private final PaymentService paymentService;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        WarehouseService warehouseService,
                        PaymentService paymentService,
                        OrderItemRepository orderItemRepository,
                        OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.warehouseService = warehouseService;
        this.paymentService = paymentService;
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
    public OrderDto getOrderById(UUID uuid) {
        try {
            return orderMapper.toDto(orderRepository.getById(uuid));
        } catch (javax.persistence.EntityNotFoundException e) {
            return null;
        }
    }

    @Override
    public OrderDto putItemToOrder(UUID orderId, UUID itemId, int amount) {
        try {
            OrderEntity order = orderRepository.getById(orderId);

            if (order.getStatus() == OrderStatus.BOOKED) {
                OrderDto orderDto = orderMapper.toDto(order);
                List<ItemQuantityRequestDTO> itemList = orderDto.getOrderItems()
                        .stream()
                        .map(orderItem ->
                                new ItemQuantityRequestDTO(orderItem.getCatalogItemId(),
                                        orderItem.getAmount()))
                        .collect(Collectors.toList());
                unbookLikeController(itemList);

                order.setStatus(OrderStatus.COLLECTING);
            }
            OrderItemEntity orderItem = new OrderItemEntity(itemId, amount);

            order.getOrderItems().add(orderItem);

            orderItemRepository.save(orderItem);
            orderRepository.save(order);
            return orderMapper.toDto(order);
        } catch (javax.persistence.EntityNotFoundException e) {
            return null;
        }
    }

    @Override
    public BookingDto bookOrder(UUID orderId) throws BookingException {
        try {
            OrderEntity order = orderRepository.getById(orderId);
            if (order.getStatus() != OrderStatus.COLLECTING) {
                return null;
            }
            OrderDto orderDto = orderMapper.toDto(order);
            List<ItemQuantityRequestDTO> itemList = orderDto.getOrderItems()
                    .stream()
                    .map(orderItem ->
                            new ItemQuantityRequestDTO(orderItem.getCatalogItemId(),
                                    orderItem.getAmount()))
                    .collect(Collectors.toList());
            BookingResponse bookingResponse = handleResponse(bookLikeController(itemList));

            if (bookingResponse.getStatus() == BookingAttemptStatus.SUCCESS) {
                order.setStatus(OrderStatus.BOOKED);
                orderRepository.save(order);
                return new BookingDto(orderId);
            }

            if (bookingResponse.getStatus() == BookingAttemptStatus.NO_RESPONSE) {
                throw new BookingException("Failed to communicate with warehouse service");
            }

            return new BookingDto(orderId, order.getOrderItems().stream().map(OrderItemEntity::getCatalogItemId).collect(Collectors.toSet()));
        } catch (javax.persistence.EntityNotFoundException e) {
            return null;
        }
    }

    @Override
    public boolean startPayment(UUID orderId) {
        OrderEntity order = orderRepository.getById(orderId);
        if (order.getStatus() != OrderStatus.BOOKED) {
            return false;
        }

        PaymentSubmissionDto response = paymentService.executePayment(orderMapper.toDto(order));

        return true;
    }

    @Override
    public BookingDto selectDeliveryTime(UUID orderId, int seconds) {
        try {
            OrderEntity order = orderRepository.getById(orderId);

            if (order.getStatus() == OrderStatus.BOOKED) {
                order.setDeliveryInfo(new Timestamp(TimeUnit.SECONDS.toMillis(seconds)));
                orderRepository.save(order);
            }
        } catch (javax.persistence.EntityNotFoundException e) {
            return null;
        }
        return new BookingDto(orderId, new HashSet<>());
    }

    private ResponseEntity<ItemResponseDTO> bookLikeController (List<ItemQuantityRequestDTO> items) {
        ResponseEntity<ItemResponseDTO> response;
        try {
            warehouseService.checkAllItems(items);
            warehouseService.checkAllQuantity(items);

            for (ItemQuantityRequestDTO item : items) {
                warehouseService.book(item);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ItemResponseDTO(400, e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new ItemResponseDTO(200, "Request executed successfully"), HttpStatus.OK);
    }

    private ResponseEntity<ItemResponseDTO> unbookLikeController (List<ItemQuantityRequestDTO> items) {
        ResponseEntity<ItemResponseDTO> response;
        try {
            warehouseService.checkAllItems(items);
            warehouseService.checkAllQuantity(items);

            for (ItemQuantityRequestDTO item : items) {
                warehouseService.unbook(item);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ItemResponseDTO(400, e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new ItemResponseDTO(200, "Request executed successfully"), HttpStatus.OK);
    }

    private BookingResponse handleResponse(ResponseEntity<ItemResponseDTO> response) {
        BookingResponse result = new BookingResponse();
        if (response.getBody() != null && response.getBody().getStatus() == 200) {
            result.setStatus(BookingAttemptStatus.SUCCESS);
        } else {
            result.setStatus(BookingAttemptStatus.FAIL);
        }
        return result;
    }
}
