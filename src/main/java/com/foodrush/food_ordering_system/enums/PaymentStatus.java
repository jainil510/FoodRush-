package com.foodrush.food_ordering_system.enums;

public enum PaymentStatus {
    PENDING("Payment Pending"),
    PAID("Payment Successful"),
    FAILED("Payment Failed"),
    REFUNDED("Payment Refunded"),
    PARTIALLY_REFUNDED("Partially Refunded");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}