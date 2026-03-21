package com.foodrush.food_ordering_system.enums;

public enum RestaurantStatus {
    PENDING("Pending Approval"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    SUSPENDED("Suspended"),
    CLOSED("Closed");

    private final String displayName;

    RestaurantStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}