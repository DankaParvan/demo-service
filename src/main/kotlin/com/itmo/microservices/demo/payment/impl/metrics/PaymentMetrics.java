package com.itmo.microservices.demo.payment.impl.metrics;

import com.itmo.microservices.demo.common.metrics.Metric;
import com.itmo.microservices.demo.common.metrics.MetricType;

public interface PaymentMetrics {
    static Metric PAYMENT_SUM = new Metric("payment_sum", "Payment sum description",
            MetricType.COUNTER, new String[] {});


    static Metric[] VALUES = new Metric[] {PAYMENT_SUM};
}
