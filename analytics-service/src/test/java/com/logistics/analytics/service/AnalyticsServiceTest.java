package com.logistics.analytics.service;

import com.logistics.analytics.entity.OrderMetric;
import com.logistics.analytics.repository.OrderMetricRepository;
import com.logistics.common.event.OrderCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private OrderMetricRepository metricRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void recordOrderCreated_incrementsTotalOrdersAndRevenue() {
        OrderMetric existing = OrderMetric.builder()
                .metricDate(LocalDate.now())
                .totalOrders(5)
                .totalRevenue(new BigDecimal("500.00"))
                .build();
        when(metricRepository.findByMetricDate(any())).thenReturn(Optional.of(existing));
        when(metricRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .totalAmount(new BigDecimal("99.99"))
                .build();
        analyticsService.recordOrderCreated(event);

        assertThat(existing.getTotalOrders()).isEqualTo(6);
        assertThat(existing.getTotalRevenue()).isEqualByComparingTo("599.99");
        verify(metricRepository).save(existing);
    }

    @Test
    void recordOrderCreated_createsNewMetricWhenNoneExists() {
        when(metricRepository.findByMetricDate(any())).thenReturn(Optional.empty());
        OrderMetric fresh = OrderMetric.builder().metricDate(LocalDate.now()).build();
        when(metricRepository.save(any())).thenReturn(fresh);

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .totalAmount(new BigDecimal("50.00"))
                .build();
        analyticsService.recordOrderCreated(event);

        verify(metricRepository, times(2)).save(any());
    }
}
