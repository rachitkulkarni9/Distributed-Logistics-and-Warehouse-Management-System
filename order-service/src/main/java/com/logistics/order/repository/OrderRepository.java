package com.logistics.order.repository;

import com.logistics.common.enums.OrderStatus;
import com.logistics.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByOrderNumber(String orderNumber);
    Page<Order> findByCustomerId(String customerId, Pageable pageable);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    Optional<Order> findByCorrelationId(String correlationId);
}
