package com.pulseguard.security;

public final class InputValidator {

    private InputValidator() { }

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }

    public static void requirePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive, got " + value);
        }
    }

    public static void requireInRange(double value, double min, double max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    String.format("%s must be between %.2f and %.2f, got %.2f", fieldName, min, max, value));
        }
    }
}
