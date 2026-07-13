package com.logistics.analytics.service;

import com.logistics.analytics.entity.OrderMetric;
import com.logistics.analytics.repository.OrderMetricRepository;
import com.logistics.common.event.OrderCreatedEvent;
import com.logistics.common.event.ShipmentCreatedEvent;
import com.logistics.common.event.ShipmentDeliveredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final OrderMetricRepository metricRepository;

    @Transactional
    public void recordOrderCreated(OrderCreatedEvent event) {
        OrderMetric metric = getOrCreateMetric(LocalDate.now());
        metric.setTotalOrders(metric.getTotalOrders() + 1);
        metric.setTotalRevenue(metric.getTotalRevenue().add(event.getTotalAmount()));
        metricRepository.save(metric);
        log.debug("Recorded order created for date={}", LocalDate.now());
    }

    @Transactional
    public void recordShipmentCreated(ShipmentCreatedEvent event) {
        OrderMetric metric = getOrCreateMetric(LocalDate.now());
        metric.setShipmentsCreated(metric.getShipmentsCreated() + 1);
        metricRepository.save(metric);
    }

    @Transactional
    public void recordShipmentDelivered(ShipmentDeliveredEvent event) {
        OrderMetric metric = getOrCreateMetric(LocalDate.now());
        metric.setShipmentsDelivered(metric.getShipmentsDelivered() + 1);
        metricRepository.save(metric);
    }

    @Transactional(readOnly = true)
    public List<OrderMetric> getDashboard(LocalDate from, LocalDate to) {
        return metricRepository.findByDateRange(from, to);
    }

    private OrderMetric getOrCreateMetric(LocalDate date) {
        return metricRepository.findByMetricDate(date)
            .orElseGet(() -> metricRepository.save(
                OrderMetric.builder().metricDate(date).build()));
    }
}
