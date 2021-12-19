package com.itmo.microservices.demo.order.impl.service;

import com.itmo.microservices.demo.order.api.event.OrderPaymentEvent;
import com.itmo.microservices.demo.order.api.event.OrderPaymentTrigger;
import com.itmo.microservices.demo.order.api.exception.BookingException;
import com.itmo.microservices.demo.order.api.dto.BookingDto;
import com.itmo.microservices.demo.payment.impl.repository.FinancialLogRecordRepository;
import com.itmo.microservices.demo.warehouse.api.model.ItemResponseDTO;
import com.itmo.microservices.demo.order.api.dto.OrderDto;
import com.itmo.microservices.demo.order.api.dto.OrderStatus;
import com.itmo.microservices.demo.order.api.exception.OrderIsNotExistException;
import com.itmo.microservices.demo.order.api.service.IOrderService;
import com.itmo.microservices.demo.order.impl.dao.OrderRepository;
import com.itmo.microservices.demo.order.impl.entity.BookingAttemptStatus;
import com.itmo.microservices.demo.order.impl.entity.BookingResponse;
import com.itmo.microservices.demo.order.impl.entity.OrderEntity;
import com.itmo.microservices.demo.order.util.mapping.OrderMapper;
import com.itmo.microservices.demo.warehouse.api.model.ItemQuantityRequestDTO;
import com.itmo.microservices.demo.warehouse.impl.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final FinancialLogRecordRepository financialLogRecordRepository;
    private final WarehouseService warehouseService;
    private final OrderMapper orderMapper;
    private final OrderPaymentTrigger orderPaymentTrigger;
    private final OrderDiscardService discardService;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        FinancialLogRecordRepository financialLogRecordRepository,
                        WarehouseService warehouseService,
                        OrderMapper orderMapper,
                        OrderPaymentTrigger orderPaymentTrigger,
                        OrderDiscardService discardService) {
        this.orderRepository = orderRepository;
        this.financialLogRecordRepository = financialLogRecordRepository;
        this.warehouseService = warehouseService;
        this.orderMapper = orderMapper;
        this.orderPaymentTrigger = orderPaymentTrigger;
        this.discardService = discardService;
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
    @Transactional
    public OrderDto putItemToOrder(UUID orderId, UUID itemId, int amount) {
        try {
            OrderEntity order = orderRepository.getById(orderId);

            if (order.getStatus() == OrderStatus.BOOKED) {
                OrderDto orderDto = orderMapper.toDto(order);
                List<ItemQuantityRequestDTO> itemList = orderDto.getItemsMap().entrySet()
                        .stream()
                        .map(orderItem ->
                                new ItemQuantityRequestDTO(orderItem.getKey(),
                                        orderItem.getValue()))
                        .collect(Collectors.toList());
                unbookLikeController(itemList);

                order.setStatus(OrderStatus.COLLECTING);
            }
            order.getItemsMap().put(itemId, amount);

            orderRepository.save(order);
            return orderMapper.toDto(order);
        } catch (javax.persistence.EntityNotFoundException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public BookingDto bookOrder(UUID orderId) throws BookingException {
        OrderEntity order = orderRepository.getById(orderId);
        if (order.getStatus() != OrderStatus.COLLECTING) {
            return null;
        }
        OrderDto orderDto = orderMapper.toDto(order);
        List<ItemQuantityRequestDTO> itemList = orderDto.getItemsMap().entrySet()
                .stream()
                .map(orderItem ->
                        new ItemQuantityRequestDTO(orderItem.getKey(),
                                orderItem.getValue()))
                .collect(Collectors.toList());
        BookingResponse bookingResponse = handleResponse(bookLikeController(itemList));

        if (bookingResponse.getStatus() == BookingAttemptStatus.SUCCESS) {
            order.setStatus(OrderStatus.BOOKED);
            // это не работает
//            discardService.scheduleOrderDiscard(this::discardOrder, orderId);
            orderRepository.save(order);
            return new BookingDto(orderId);
        }

        if (bookingResponse.getStatus() == BookingAttemptStatus.NO_RESPONSE) {
            throw new BookingException("Failed to communicate with warehouse service");
        }

        return new BookingDto(orderId, order.getItemsMap().keySet());
    }

    @Override
    public boolean startPayment(UUID orderId) {
        OrderEntity order = orderRepository.getById(orderId);
        if (order.getStatus() != OrderStatus.BOOKED) {
            return false;
        }

        orderPaymentTrigger.onOrderPaymentHandled(
                new OrderPaymentEvent(order.getId(), "Ask PaymentService for payment")
        );

        return true;
    }

    @Override
    public BookingDto selectDeliveryTime(UUID orderId, int seconds) {
        OrderEntity order = orderRepository.getById(orderId);

        if (order.getStatus() == OrderStatus.BOOKED) {
            order.setDeliveryDuration(seconds);
            orderRepository.save(order);
        }

        return new BookingDto(orderId, new HashSet<>());
    }

    @Transactional
    ResponseEntity<ItemResponseDTO> bookLikeController(List<ItemQuantityRequestDTO> items) {
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

    @Transactional
    ResponseEntity<ItemResponseDTO> unbookLikeController(List<ItemQuantityRequestDTO> items) {
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

    // а что значит DISCARD без использования внешней системы уведомлений? оставлю пока так
    // юзкейс - если забронировали, но не успели оплатить - сбрасываем в статус коллектинг
    // при этом отменяем бронирование на складе
    public void discardOrder(UUID orderId) {
        OrderEntity order = orderRepository.getById(orderId);
        if (order.getStatus() == OrderStatus.BOOKED) {
            order.setStatus(OrderStatus.COLLECTING);
            orderRepository.save(order);
            List<ItemQuantityRequestDTO> itemList = order.getOrderItems()
                    .stream()
                    .map(orderItem ->
                            new ItemQuantityRequestDTO(orderItem.getCatalogItemId(),
                                    orderItem.getAmount()))
                    .collect(Collectors.toList());
            unbookLikeController(itemList);
        }
    }
}
