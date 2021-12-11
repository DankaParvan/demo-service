package com.itmo.microservices.demo.order.util.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class OrderDiscardJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        TimerInfo info = (TimerInfo) jobExecutionContext.getJobDetail().getJobDataMap().get(OrderDiscardJob.class.getSimpleName());
        info.getDiscardCallback().accept(info.getOrderID());
    }
}
