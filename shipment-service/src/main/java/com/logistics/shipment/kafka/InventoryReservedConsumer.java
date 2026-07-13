package com.logistics.shipment.kafka;

import com.logistics.common.config.KafkaTopics;
import com.logistics.common.event.InventoryReservedEvent;
import com.logistics.common.event.ShipmentCreatedEvent;
import com.logistics.common.enums.ShipmentStatus;
import com.logistics.common.util.TrackingNumberGenerator;
import com.logistics.shipment.entity.Shipment;
import com.logistics.shipment.kafka.ShipmentEventProducer;
import com.logistics.shipment.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryReservedConsumer {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentEventProducer eventProducer;

    @KafkaListener(topics = KafkaTopics.INVENTORY_RESERVED, groupId = "shipment-service-group")
    @Transactional
    public void onInventoryReserved(@Payload InventoryReservedEvent event, Acknowledgment ack) {
        log.info("Creating shipment for orderId={}", event.getOrderId());

        Shipment shipment = Shipment.builder()
            .orderId(event.getOrderId())
            .trackingNumber(TrackingNumberGenerator.generate())
            .carrier("LogiExpress") // TODO: select carrier via rules engine
            .status(ShipmentStatus.CREATED)
            .originWarehouseId(event.getWarehouseId())
            .estimatedDeliveryDate(LocalDate.now().plusDays(5))
            .correlationId(event.getCorrelationId())
            .build();

        shipment = shipmentRepository.save(shipment);

        eventProducer.publishCreated(ShipmentCreatedEvent.builder()
            .correlationId(event.getCorrelationId())
            .shipmentId(shipment.getId())
            .orderId(shipment.getOrderId())
            .trackingNumber(shipment.getTrackingNumber())
            .carrier(shipment.getCarrier())
            .estimatedDeliveryDate(shipment.getEstimatedDeliveryDate())
            .build());

        ack.acknowledge();
    }
}
