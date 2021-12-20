package com.itmo.microservices.demo.order.util.scheduler;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;
import java.util.function.Consumer;

@Builder
@Getter
public class TimerInfo {
    private final int totalFireCount;
    private final boolean runForever;
    private final long repeatInterval;
    private final long initialOffset;
    private final Consumer<UUID> discardCallback;
    private final UUID orderID;
}
