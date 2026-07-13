package com.logistics.warehouse.repository;

import com.logistics.warehouse.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
    Optional<Warehouse> findByCode(String code);
    List<Warehouse> findByActiveTrue();
    boolean existsByCode(String code);
}
