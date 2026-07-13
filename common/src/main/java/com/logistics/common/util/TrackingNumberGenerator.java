package com.logistics.common.util;

import java.security.SecureRandom;

/**
 * Generates carrier-style alphanumeric tracking numbers.
 * Format: LGS + 16 uppercase alphanumeric characters
 */
public final class TrackingNumberGenerator {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private TrackingNumberGenerator() {}

    public static String generate() {
        StringBuilder sb = new StringBuilder("LGS");
        for (int i = 0; i < LENGTH; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
