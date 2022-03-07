package com.itmo.microservices.demo.payment.impl.metrics;

import com.itmo.microservices.demo.common.metrics.Metric;
import com.itmo.microservices.demo.common.metrics.MetricType;

public interface PaymentMetrics {
    static Metric REVENUE = new Metric("revenue", "Payment sum description",
            MetricType.COUNTER, new String[] {});

    static Metric EXTERNAL_SYSTEM_EXPENSE = new Metric("external_system_expense", "Количество денег которые были потрачены",
            MetricType.COUNTER, new String[]{"externalSystemType"});

    static Metric REFUNDED_MONEY_AMOUNT = new Metric("refunded_money_amount", "Количество денег возвращенных пользователю",
            MetricType.COUNTER, new String[]{"refundReason"});

    static Metric ORDERS_IN_STATUS = new Metric("orders_in_status", "Количество заказов в каждом статусе",
            MetricType.GAUGE, new String[] {"status"});


    static Metric[] VALUES = new Metric[] {REVENUE, EXTERNAL_SYSTEM_EXPENSE, REFUNDED_MONEY_AMOUNT, ORDERS_IN_STATUS};
}
