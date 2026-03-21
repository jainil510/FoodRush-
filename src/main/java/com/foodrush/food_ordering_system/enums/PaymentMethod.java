package com.foodrush.food_ordering_system.enums;

public enum PaymentMethod {
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    CASH("Cash on Delivery"),
    DIGITAL_WALLET("Digital Wallet"),
    UPI("UPI"),
    NET_BANKING("Net Banking");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}