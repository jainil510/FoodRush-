package com.foodrush.food_ordering_system.dto.request;

import com.foodrush.food_ordering_system.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {

    @NotNull(message = "Order status is required")
    private OrderStatus orderStatus;

    private String reason;
    private Integer actualDeliveryTime;
    private String driverName;
    private String driverPhone;
}