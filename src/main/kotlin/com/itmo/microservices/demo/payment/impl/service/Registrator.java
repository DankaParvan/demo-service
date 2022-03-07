package com.itmo.microservices.demo.payment.impl.service;

import com.itmo.microservices.demo.common.metrics.Metric;
import com.itmo.microservices.demo.common.metrics.MetricsCollector;
import com.itmo.microservices.demo.payment.impl.metrics.PaymentMetrics;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
public class Registrator {
    private static boolean registred = false;
    private MetricsCollector metricsCollector;

    public Registrator(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    @PostConstruct
    public void onCreate() {
        if (registred) {
            return;
        }
        registred = true;
        try {
            for (Metric metric : PaymentMetrics.VALUES) {
                String[] tags = new String[metric.getTags().length];
                Arrays.fill(tags, "");
                metricsCollector.handle(metric, 1, tags);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

}
