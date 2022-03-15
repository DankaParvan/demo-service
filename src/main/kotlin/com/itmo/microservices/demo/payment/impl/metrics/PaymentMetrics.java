package com.itmo.microservices.demo.payment.impl.metrics;

import com.itmo.microservices.demo.common.metrics.Metric;
import com.itmo.microservices.demo.common.metrics.MetricType;

public interface PaymentMetrics {
    static Metric REVENUE = new Metric("revenue", "Payment sum description",
            MetricType.COUNTER, new String[]{});

    static Metric EXTERNAL_SYSTEM_EXPENSE = new Metric("external_system_expense", "Количество денег которые были потрачены",
            MetricType.COUNTER, new String[]{"externalSystemType"});

    static Metric REFUNDED_MONEY_AMOUNT = new Metric("refunded_money_amount", "Количество денег возвращенных пользователю",
            MetricType.COUNTER, new String[]{"refundReason"});

    static Metric ORDERS_IN_STATUS = new Metric("orders_in_status", "Количество заказов в каждом статусе",
            MetricType.GAUGE, new String[]{"status"});

    static Metric CATALOG_SHOWN = create("catalog_shown", "Количество просмотров каталога продукции",
            MetricType.COUNTER);

    static Metric ITEM_ADDED = create("item_added", "Количество добавлений товара (товаров) в заказ",
            MetricType.COUNTER);

    static Metric ORDER_CREATED = create("order_created", "Создание нового заказа", MetricType.COUNTER);

    static Metric ITEM_BOOK_REQUEST = create("item_book_request", "Количество запросов на бронирование товаров для заказа",
            MetricType.COUNTER, "result", "status");

    static Metric FINALIZATION_ATTEMPT = create("finalization_attempt", "Количество запросов на финализацию заказа",
            MetricType.COUNTER, "result", "status");

    static Metric FINALIZATION_DURATION = create("finalization_duration", "Длительность процесса финализации",
            MetricType.GAUGE);

    static Metric CURRENT_SHIPPING_ORDERS = create("current_shipping_orders", "Количество заказов, которые прямо сейчас находятся в доставке",
            MetricType.GAUGE);

    static Metric SHIPPING_ORDERS_TOTAL = create("shipping_orders_total", "Количество заказов, переданных в доставку",
            MetricType.COUNTER);

    static Metric TIMELOT_SET_REQUEST_COUNT = create("timelot_set_request_count", "Время доставки выставлено (выбран таймслот) - количество запросов",
            MetricType.COUNTER);

    static Metric ADD_TO_FINILIZED_ORDER_REQEUEST = create("add_to_finilized_order_request", "Количество запросов на изменение заказа (добавление товара) после финализации!",
            MetricType.COUNTER);

    static Metric CURRENT_ABANDONED_ORDER_NUM = create("current_abandoned_order_num", "Количество не финализированных корзин",
            MetricType.GAUGE);

    static Metric CURRENT_RESTORED_ORDER_NUM = create("current_restored_order_num", "Количество востановленных корзин",
            MetricType.COUNTER);

    static Metric DISCARDED_ORDERS = create("discarded_orders", "Количество корзин которые были удалны",
            MetricType.COUNTER);

    static Metric ORDER_STATUS_CHANGED = create("order_status_changed", "Изменение статуса заказа",
            MetricType.COUNTER, "fromState", "toState");

    static Metric EXPIRED_DELIVERY_ORDER = create("expired_delivery_order", "Количество заказов, которые перешли в возврат из-за сторонней системы",
            MetricType.COUNTER, "accountId");

    static Metric REFUNDED_DUE_TO_WRONG_TIME_PREDICTION_ORDER = create("refunded_due_to_wrong_time_prediction_order",
            "missed shot due to predict", MetricType.COUNTER);

    static Metric AVG_BOOKING_TO_PAYED_TIME = create("avg_booking_to_payed_time", "Среднее время, которое проходит от бронирования до оплаты заказа",
            MetricType.GAUGE);

    static Metric create(String name, String description, MetricType metricType, String... tags) {
        return new Metric(name, description, metricType, tags);
    }

    static Metric[] VALUES = {
            REVENUE,
            EXTERNAL_SYSTEM_EXPENSE,
            REFUNDED_MONEY_AMOUNT,
            ORDERS_IN_STATUS,
            CATALOG_SHOWN,
            ITEM_ADDED,
            ORDER_CREATED,
            ITEM_BOOK_REQUEST,
            FINALIZATION_ATTEMPT,
            FINALIZATION_DURATION,
            CURRENT_SHIPPING_ORDERS,
            SHIPPING_ORDERS_TOTAL,
            TIMELOT_SET_REQUEST_COUNT,
            ADD_TO_FINILIZED_ORDER_REQEUEST,
            CURRENT_ABANDONED_ORDER_NUM,
            CURRENT_RESTORED_ORDER_NUM,
            DISCARDED_ORDERS,
            ORDER_STATUS_CHANGED,
            EXPIRED_DELIVERY_ORDER,
            REFUNDED_DUE_TO_WRONG_TIME_PREDICTION_ORDER,
            AVG_BOOKING_TO_PAYED_TIME,
    };


}
