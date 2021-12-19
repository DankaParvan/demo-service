package com.itmo.microservices.demo.order.impl.service;

import com.itmo.microservices.demo.order.util.scheduler.OrderDiscardJob;
import com.itmo.microservices.demo.order.util.scheduler.TimerInfo;
import com.itmo.microservices.demo.order.util.scheduler.TimerUtil;
import org.quartz.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
public class OrderDiscardService {
    private final Scheduler scheduler;

    OrderDiscardService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostConstruct
    public void init() {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void preDestroy() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void scheduleOrderDiscard(Consumer<UUID> discardCallback, UUID orderID) {
        TimerInfo timerInfo = TimerInfo.builder()
                .totalFireCount(1)
                .initialOffset(TimeUnit.SECONDS.toMillis(10))
                .runForever(false)
                .discardCallback(discardCallback)
                .orderID(orderID)
                .build();

        final JobDetail jobDetail = TimerUtil.buildJobDetail(OrderDiscardJob.class, timerInfo);
        final Trigger trigger = TimerUtil.buildTrigger(OrderDiscardJob.class, timerInfo);

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e){
            e.printStackTrace();
        }
    }
}
