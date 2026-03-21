package com.foodrush.food_ordering_system.enums;

public enum OrderStatus {
    PENDING("Pending Confirmation"),
    CONFIRMED("Order Confirmed"),
    PREPARING("Preparing Food"),
    READY_FOR_PICKUP("Ready for Pickup"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled"),
    REFUNDED("Refunded");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}