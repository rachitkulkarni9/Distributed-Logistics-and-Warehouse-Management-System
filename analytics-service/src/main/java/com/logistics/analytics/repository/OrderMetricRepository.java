package com.logistics.analytics.repository;

import com.logistics.analytics.entity.OrderMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderMetricRepository extends JpaRepository<OrderMetric, UUID> {

    Optional<OrderMetric> findByMetricDate(LocalDate date);

    @Query("SELECT m FROM OrderMetric m WHERE m.metricDate BETWEEN :from AND :to ORDER BY m.metricDate")
    List<OrderMetric> findByDateRange(LocalDate from, LocalDate to);
}
