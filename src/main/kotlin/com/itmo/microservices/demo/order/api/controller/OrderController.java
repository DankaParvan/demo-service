package com.itmo.microservices.demo.order.api.controller;

import com.itmo.microservices.demo.order.api.BookingException;
import com.itmo.microservices.demo.order.api.dto.BookingDto;
import com.itmo.microservices.demo.order.api.dto.OrderDto;
import com.itmo.microservices.demo.order.impl.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService service;

    @Autowired
    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(
            summary = "Create order",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200", content = {@Content}),
                    @ApiResponse(description = "Something went wrong", responseCode = "400", content = {@Content})},
            security = {@SecurityRequirement(name = "bearerAuth")})
    public OrderDto createOrder() {
        return service.createOrder();
    }

    @GetMapping("/{orderId}")
    @Operation(
            summary = "Get order",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200", content = {@Content}),
                    @ApiResponse(description = "Something went wrong", responseCode = "400", content = {@Content})},
            security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<OrderDto> getOrder(@PathVariable("orderId") UUID uuid) {
        OrderDto order = service.getOrderById(uuid);
        return new ResponseEntity<>(order, order == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }

    @PutMapping("/{orderId}/items/{itemId}")
    @Operation(
            summary = "Put the item in the cart",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Something went wrong", responseCode = "400")},
            security = {@SecurityRequirement(name = "bearerAuth")})
    public void updateOrder(@PathVariable("orderId") UUID orderId,
                            @PathVariable("itemId") UUID itemId,
                            @RequestParam(name = "amount") int amount) {
        service.putItemToOrder(orderId, itemId, amount);
    }

    @PostMapping("/{orderId}/bookings")
    @Operation(
            summary = "Book",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200", content = {@Content}),
                    @ApiResponse(description = "Something went wrong", responseCode = "400", content = {@Content})},
            security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<BookingDto> bookOrder(@PathVariable("orderId") UUID orderId) {
        BookingDto booking;
        try {
            booking = service.bookOrder(orderId);
        } catch (BookingException e) {
            return new ResponseEntity<>(null, HttpStatus.REQUEST_TIMEOUT);
        }
        return new ResponseEntity<>(booking, booking == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }

    @PostMapping("/{orderId}/delivery")
    @Operation(
            summary = "Delivery time selection",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200", content = {@Content}),
                    @ApiResponse(description = "Something went wrong", responseCode = "400", content = {@Content})},
            security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<BookingDto> selectDeliveryTime(@PathVariable("orderId") UUID orderId,
                                                         @RequestParam(name = "slot") int seconds) {
        BookingDto booking = service.selectDeliveryTime(orderId, seconds);
        return new ResponseEntity<>(booking, booking == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }
}
