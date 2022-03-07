package com.itmo.microservices.demo.common.metrics;

import java.util.Arrays;

public class Metric {
    private String name;
    private String description;
    private MetricType metricType;
    private String[] tags;


    public Metric(String name, String description, MetricType metricType, String[] tags) {
        this.name = name;
        this.description = description;
        this.metricType = metricType;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public String[] getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "Metric{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", metricType=" + metricType +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }
}
