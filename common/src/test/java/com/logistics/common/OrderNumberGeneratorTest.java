package com.logistics.common;

import com.logistics.common.util.OrderNumberGenerator;
import com.logistics.common.util.TrackingNumberGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OrderNumberGeneratorTest {

    @Test
    void orderNumber_hasCorrectPrefix() {
        String number = OrderNumberGenerator.generate();
        assertThat(number).startsWith("ORD-");
    }

    @Test
    void orderNumber_isUnique_acrossMultipleCalls() {
        Set<String> numbers = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            numbers.add(OrderNumberGenerator.generate());
        }
        assertThat(numbers).hasSize(1000);
    }

    @Test
    void trackingNumber_hasCorrectPrefix() {
        String tracking = TrackingNumberGenerator.generate();
        assertThat(tracking).startsWith("LGS");
        assertThat(tracking).hasSize(19); // "LGS" + 16 chars
    }

    @Test
    void trackingNumber_isUnique_acrossMultipleCalls() {
        Set<String> numbers = new HashSet<>();
        for (int i = 0; i < 500; i++) {
            numbers.add(TrackingNumberGenerator.generate());
        }
        assertThat(numbers).hasSize(500);
    }
}
