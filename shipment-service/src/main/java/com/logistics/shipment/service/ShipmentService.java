package com.logistics.shipment.service;

import com.logistics.common.dto.shipment.ShipmentDto;
import com.logistics.common.enums.ShipmentStatus;
import com.logistics.common.event.ShipmentDeliveredEvent;
import com.logistics.common.event.ShipmentDispatchedEvent;
import com.logistics.common.exception.ResourceNotFoundException;
import com.logistics.shipment.entity.Shipment;
import com.logistics.shipment.kafka.ShipmentEventProducer;
import com.logistics.shipment.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentEventProducer eventProducer;

    @Transactional(readOnly = true)
    public Shipment findById(UUID id) {
        return shipmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", id));
    }

    @Transactional(readOnly = true)
    public Shipment findByTracking(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment", "trackingNumber", trackingNumber));
    }

    @Transactional
    public Shipment dispatch(UUID id) {
        Shipment shipment = findById(id);
        shipment.setStatus(ShipmentStatus.DISPATCHED);
        shipment.setDispatchedAt(Instant.now());
        shipmentRepository.save(shipment);

        eventProducer.publishDispatched(ShipmentDispatchedEvent.builder()
            .correlationId(shipment.getCorrelationId())
            .shipmentId(shipment.getId())
            .orderId(shipment.getOrderId())
            .trackingNumber(shipment.getTrackingNumber())
            .carrier(shipment.getCarrier())
            .dispatchedAt(shipment.getDispatchedAt())
            .currentLocation("Origin Warehouse")
            .build());

        return shipment;
    }

    @Transactional
    public Shipment markDelivered(UUID id) {
        Shipment shipment = findById(id);
        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipment.setDeliveredAt(Instant.now());
        shipmentRepository.save(shipment);

        eventProducer.publishDelivered(ShipmentDeliveredEvent.builder()
            .correlationId(shipment.getCorrelationId())
            .shipmentId(shipment.getId())
            .orderId(shipment.getOrderId())
            .trackingNumber(shipment.getTrackingNumber())
            .deliveredAt(shipment.getDeliveredAt())
            .build());

        return shipment;
    }

    public ShipmentDto toDto(Shipment s) {
        return ShipmentDto.builder()
            .id(s.getId()).orderId(s.getOrderId()).trackingNumber(s.getTrackingNumber())
            .carrier(s.getCarrier()).status(s.getStatus()).originWarehouseId(s.getOriginWarehouseId())
            .destinationAddress(s.getDestinationAddress())
            .estimatedDeliveryDate(s.getEstimatedDeliveryDate())
            .dispatchedAt(s.getDispatchedAt()).deliveredAt(s.getDeliveredAt())
            .createdAt(s.getCreatedAt()).updatedAt(s.getUpdatedAt()).build();
    }
}
