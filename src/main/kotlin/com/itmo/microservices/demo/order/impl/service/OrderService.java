package com.itmo.microservices.demo.order.impl.service;

import com.itmo.microservices.demo.order.api.event.OrderPaymentEvent;
import com.itmo.microservices.demo.order.api.event.OrderPaymentTrigger;
import com.itmo.microservices.demo.order.api.exception.BookingException;
import com.itmo.microservices.demo.order.api.dto.BookingDto;
import com.itmo.microservices.demo.payment.api.model.PaymentLogRecordDto;
import com.itmo.microservices.demo.payment.api.model.PaymentStatus;
import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.demo.payment.impl.entity.FinancialLogRecordEntity;
import com.itmo.microservices.demo.payment.impl.repository.FinancialLogRecordRepository;
import com.itmo.microservices.demo.payment.impl.repository.PaymentRepository;
import com.itmo.microservices.demo.warehouse.api.model.ItemResponseDTO;
import com.itmo.microservices.demo.order.api.dto.OrderDto;
import com.itmo.microservices.demo.order.api.dto.OrderStatus;
import com.itmo.microservices.demo.order.api.exception.OrderIsNotExistException;
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
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final FinancialLogRecordRepository financialLogRecordRepository;
    private final WarehouseService warehouseService;
    private final PaymentService paymentService;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderPaymentTrigger orderPaymentTrigger;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        FinancialLogRecordRepository financialLogRecordRepository,
                        WarehouseService warehouseService,
                        PaymentService paymentService,
                        OrderItemRepository orderItemRepository,
                        OrderMapper orderMapper,
                        OrderPaymentTrigger orderPaymentTrigger) {
        this.orderRepository = orderRepository;
        this.financialLogRecordRepository = financialLogRecordRepository;
        this.warehouseService = warehouseService;
        this.paymentService = paymentService;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
        this.orderPaymentTrigger = orderPaymentTrigger;
    }

    @Override
    public OrderDto createOrder() {
        OrderEntity newOrder = new OrderEntity();
        UUID orderId = orderRepository.save(newOrder).getUuid();

//        return orderMapper.toDto(newOrder);

        return new OrderDto(
                orderId,
                System.currentTimeMillis(),
                newOrder.getStatus(),
                Collections.emptyMap(),
                null,
                Collections.emptyList()
        );
    }

    @Override
    public OrderDto getOrderById(UUID uuid) throws OrderIsNotExistException {
        var orderMapperIsAVeryBigPileOfSHIIIIIT = orderRepository.findById(uuid);

        Map<UUID, Integer> map = new HashMap<>();

        List<OrderItemEntity> orderItems = orderMapperIsAVeryBigPileOfSHIIIIIT.get().getOrderItems();

        for (OrderItemEntity orderItem : orderItems) {
            map.put(orderItem.getCatalogItemId(), orderItem.getAmount());
        }

        var order = new OrderDto(
                orderMapperIsAVeryBigPileOfSHIIIIIT.get().getUuid(),
                System.currentTimeMillis(),
                orderMapperIsAVeryBigPileOfSHIIIIIT.get().getStatus(),
                map,
                null,
                Collections.emptyList()
        );
        return order;
    }

    @Override
    public OrderDto putItemToOrder(UUID orderId, UUID itemId, int amount) {
        try {
            OrderEntity order = orderRepository.findById(orderId).get();
            order.setStatus(OrderStatus.BOOKED);

            Map<UUID, Integer> map = new HashMap<>();

            List<OrderItemEntity> orderItems = order.getOrderItems();

            for (OrderItemEntity orderItem : orderItems) {
                map.put(orderItem.getCatalogItemId(), orderItem.getAmount());
            }

            if (order.getStatus() == OrderStatus.BOOKED) {
                OrderDto orderDto = new OrderDto(
                        orderId,
                        order.getTimeCreated().getLong(ChronoField.MILLI_OF_SECOND),
                        order.getStatus(),
                        map,
                        null,
                        Collections.emptyList());

                List<ItemQuantityRequestDTO> itemQuantityRequests = new ArrayList<>();
                for (UUID key : orderDto.getItemsMap().keySet()) {
                    ItemQuantityRequestDTO itemQuantityRequest = new ItemQuantityRequestDTO(
                            key,
                            orderDto.getItemsMap().get(key)
                    );
                    itemQuantityRequests.add(itemQuantityRequest);
                }

                unbookLikeController(itemQuantityRequests);

                order.setStatus(OrderStatus.COLLECTING);
            }
            OrderItemEntity orderItem = new OrderItemEntity(itemId, amount);

            order.getOrderItems().add(orderItem);

            orderItemRepository.save(orderItem);
            orderRepository.save(order);

            return new OrderDto(
                    orderId,
                    order.getTimeCreated().getLong(ChronoField.MILLI_OF_SECOND),
                    order.getStatus(),
                    map,
                    null,
                    Collections.emptyList()
            );
        } catch (javax.persistence.EntityNotFoundException e) {
            return null;
        }
    }

    @Override
    public BookingDto bookOrder(UUID orderId) throws BookingException {
        OrderEntity order = orderRepository.getById(orderId);
        if (order.getStatus() != OrderStatus.COLLECTING) {
            return null;
        }
        OrderDto orderDto = orderMapper.toDto(order);

        List<ItemQuantityRequestDTO> itemList = new ArrayList<>();
        for (UUID key : orderDto.getItemsMap().keySet()) {
            ItemQuantityRequestDTO itemQuantityRequest = new ItemQuantityRequestDTO(
                    key,
                    orderDto.getItemsMap().get(key)
            );
            itemList.add(itemQuantityRequest);
        }

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
    }

    @Override
    public boolean startPayment(UUID orderId) {
        var orderMapperIsAVeryBigPileOfSHIIIIIT = orderRepository.findById(orderId);
        Map<UUID, Integer> map = new HashMap<>();

        List<OrderItemEntity> orderItems = orderMapperIsAVeryBigPileOfSHIIIIIT.get().getOrderItems();

        for (OrderItemEntity orderItem : orderItems) {
            map.put(orderItem.getCatalogItemId(), orderItem.getAmount());
        }

        var order = new OrderDto(
                orderMapperIsAVeryBigPileOfSHIIIIIT.get().getUuid(),
                System.currentTimeMillis(),
                orderMapperIsAVeryBigPileOfSHIIIIIT.get().getStatus(),
                map,
                null,
                Collections.emptyList()
        );
        order.status = OrderStatus.BOOKED;

        if (order.getStatus() != OrderStatus.BOOKED) {
            return false;
        }

        orderPaymentTrigger.onOrderPaymentHandled(
                new OrderPaymentEvent(order.getId(), "Ask PaymentService for payment")
        );

        PaymentSubmissionDto response = paymentService.executePayment(order);

        return true;
    }

    @Override
    public BookingDto selectDeliveryTime(UUID orderId, int seconds) {
        OrderEntity order = orderRepository.getById(orderId);

        if (order.getStatus() == OrderStatus.BOOKED) {
            order.setDeliveryInfo(new Timestamp(TimeUnit.SECONDS.toMillis(seconds)));
            orderRepository.save(order);
        }

        return new BookingDto(orderId, new HashSet<>());
    }

    private ResponseEntity<ItemResponseDTO> bookLikeController(List<ItemQuantityRequestDTO> items) {
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

    private ResponseEntity<ItemResponseDTO> unbookLikeController(List<ItemQuantityRequestDTO> items) {
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
