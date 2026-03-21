package com.foodrush.food_ordering_system.dto.response;

import com.foodrush.food_ordering_system.enums.OrderStatus;
import com.foodrush.food_ordering_system.enums.PaymentMethod;
import com.foodrush.food_ordering_system.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private Long userId;
    private String userName;
    private Long restaurantId;
    private String restaurantName;
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryState;
    private String deliveryZipCode;
    private String deliveryPhone;
    private BigDecimal subtotal;
    private BigDecimal deliveryFee;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal tipAmount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;
    private Integer estimatedDeliveryTime;
    private Integer actualDeliveryTime;
    private String specialInstructions;
    private String driverName;
    private String driverPhone;
    private String createdAt;
    private String updatedAt;
    private String deliveredAt;

    public OrderResponse(Long id, Long restaurantId, String restaurantName,
                         OrderStatus orderStatus, BigDecimal totalAmount,
                         String createdAt) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public static OrderResponse fromOrder(com.foodrush.food_ordering_system.entity.Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUser() != null ? order.getUser().getId() : null,
                order.getUser() != null ? order.getUser().getFirstName() + " " + order.getUser().getLastName() : null,
                order.getRestaurant() != null ? order.getRestaurant().getId() : null,
                order.getRestaurant() != null ? order.getRestaurant().getName() : null,
                order.getDeliveryAddress(),
                order.getDeliveryCity(),
                order.getDeliveryState(),
                order.getDeliveryZipCode(),
                order.getDeliveryPhone(),
                order.getSubtotal(),
                order.getDeliveryFee(),
                order.getTaxAmount(),
                order.getTotalAmount(),
                order.getTipAmount(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getOrderStatus(),
                order.getEstimatedDeliveryTime(),
                order.getActualDeliveryTime(),
                order.getSpecialInstructions(),
                order.getDriverName(),
                order.getDriverPhone(),
                order.getCreatedAt() != null ? order.getCreatedAt().toString() : null,
                order.getUpdatedAt() != null ? order.getUpdatedAt().toString() : null,
                order.getDeliveredAt() != null ? order.getDeliveredAt().toString() : null
        );
    }

    public static OrderResponse minimalFromOrder(com.foodrush.food_ordering_system.entity.Order order) {
        return new OrderResponse(
                order.getId(),
                order.getRestaurant() != null ? order.getRestaurant().getId() : null,
                order.getRestaurant() != null ? order.getRestaurant().getName() : null,
                order.getOrderStatus(),
                order.getTotalAmount(),
                order.getCreatedAt() != null ? order.getCreatedAt().toString() : null
        );
    }

    public static OrderResponse restaurantViewFromOrder(com.foodrush.food_ordering_system.entity.Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUser() != null ? order.getUser().getId() : null,
                order.getUser() != null ? order.getUser().getFirstName() + " " + order.getUser().getLastName() : null,
                order.getRestaurant() != null ? order.getRestaurant().getId() : null,
                order.getRestaurant() != null ? order.getRestaurant().getName() : null,
                order.getDeliveryAddress(),
                order.getDeliveryCity(),
                order.getDeliveryState(),
                order.getDeliveryZipCode(),
                order.getDeliveryPhone(),
                order.getSubtotal(),
                order.getDeliveryFee(),
                order.getTaxAmount(),
                order.getTotalAmount(),
                order.getTipAmount(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getOrderStatus(),
                order.getEstimatedDeliveryTime(),
                order.getActualDeliveryTime(),
                order.getSpecialInstructions(),
                order.getDriverName(),
                order.getDriverPhone(),
                order.getCreatedAt() != null ? order.getCreatedAt().toString() : null,
                order.getUpdatedAt() != null ? order.getUpdatedAt().toString() : null,
                order.getDeliveredAt() != null ? order.getDeliveredAt().toString() : null
        );
    }
}