package com.logistics.shipment.service;

import com.logistics.common.enums.ShipmentStatus;
import com.logistics.common.exception.ResourceNotFoundException;
import com.logistics.shipment.entity.Shipment;
import com.logistics.shipment.kafka.ShipmentEventProducer;
import com.logistics.shipment.repository.ShipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock private ShipmentRepository shipmentRepository;
    @Mock private ShipmentEventProducer eventProducer;
    @InjectMocks private ShipmentService shipmentService;

    @Test
    void dispatch_updatesStatusAndPublishesEvent() {
        UUID id = UUID.randomUUID();
        Shipment shipment = Shipment.builder().id(id).orderId(UUID.randomUUID())
            .trackingNumber("LGS123").carrier("LogiExpress")
            .status(ShipmentStatus.CREATED).correlationId("corr-1").build();
        when(shipmentRepository.findById(id)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any())).thenReturn(shipment);

        Shipment result = shipmentService.dispatch(id);

        assertThat(result.getStatus()).isEqualTo(ShipmentStatus.DISPATCHED);
        verify(eventProducer).publishDispatched(any());
    }

    @Test
    void findById_whenMissing_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(shipmentRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> shipmentService.findById(id))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
