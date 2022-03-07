package com.itmo.microservices.demo.common.metrics;

import com.itmo.microservices.commonlib.metrics.CommonMetricsCollector;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.SimpleCollector;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MetricsCollector extends CommonMetricsCollector {
    private static final String SERVICE_NAME = "p09";
    public static final String SERVICE_NAME_PROMETHEUS_TAG = "serviceName";

    private PrometheusMeterRegistry registry;
    private Map<String, SimpleCollector<?>> metricsPrometheus = new HashMap<>();

    public MetricsCollector(PrometheusMeterRegistry registry) {
        super(SERVICE_NAME);
        this.registry = registry;

        Metrics.addRegistry(registry);
    }


    public void handle(Metric metric, double value, String... tags) {
        SimpleCollector rawCollector = metricsPrometheus.get(metric.getName());
        if (rawCollector == null) {
            throw new IllegalStateException("Unknown metric " + metric);
        }
        switch (metric.getMetricType()) {
            case COUNTER: {
                Counter counter = (Counter) rawCollector;
                counter.labels(addServiceNameLabel(tags, SERVICE_NAME)).inc(value);
                break;
            }
            case GAUGE: {
                Gauge gauge = (Gauge) rawCollector;
                if (value > 0) {
                    gauge.labels(addServiceNameLabel(tags, SERVICE_NAME)).inc(value);
                } else {
                    gauge.labels(addServiceNameLabel(tags, SERVICE_NAME)).dec(-value);
                }
                break;
            }
            default: throw new IllegalStateException("Unknown metric type of " + metric);
        }
    }

    public void register(Metric... metrics) {
        for (Metric metric : metrics) {
            MetricType metricType = metric.getMetricType();
            SimpleCollector<?> collector;
            switch (metricType) {
                case COUNTER: {
                    collector = Counter.build()
                            .name(metric.getName())
                            .help(metric.getDescription())
                            .labelNames(addServiceNameLabel(metric.getTags(), SERVICE_NAME_PROMETHEUS_TAG))
                            .register(registry.getPrometheusRegistry());
                    break;
                }
                case GAUGE: {
                    collector = Gauge.build()
                            .name(metric.getName())
                            .help(metric.getDescription())
                            .labelNames(addServiceNameLabel(metric.getTags(), SERVICE_NAME_PROMETHEUS_TAG))
                            .register(registry.getPrometheusRegistry());
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unhandled metric type " + metricType);
            }

            metricsPrometheus.put(metric.getName(), collector);
        }
    }

    private String[] addServiceNameLabel(String[] rawTags, String tag) {
        String[] result = new String[rawTags.length + 1];
        result[0] = tag;
        for (int i = 0; i < rawTags.length; i++) {
            result[i + 1] = rawTags[i];
        }
        return result;
    }
}
