package com.itmo.microservices.demo.order.impl.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    BookingAttemptStatus status;
    Set<UUID> failedItems = new HashSet<>();
}
