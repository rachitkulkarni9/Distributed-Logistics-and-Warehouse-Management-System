package com.logistics.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates human-readable, sortable order numbers.
 * Format: ORD-YYYYMMDD-NNNNNN
 */
public final class OrderNumberGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final AtomicLong SEQUENCE = new AtomicLong(0);

    private OrderNumberGenerator() {}

    public static String generate() {
        String date = LocalDate.now().format(DATE_FORMAT);
        long seq = SEQUENCE.incrementAndGet() % 1_000_000;
        return String.format("ORD-%s-%06d", date, seq);
    }
}
