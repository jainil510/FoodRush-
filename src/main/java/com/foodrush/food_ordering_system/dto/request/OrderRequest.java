package com.foodrush.food_ordering_system.dto.request;

import com.foodrush.food_ordering_system.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequest {

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotBlank(message = "Delivery address is required")
    @Size(max = 200, message = "Delivery address must not exceed 200 characters")
    private String deliveryAddress;

    @NotBlank(message = "Delivery city is required")
    @Size(max = 50, message = "Delivery city must not exceed 50 characters")
    private String deliveryCity;

    @NotBlank(message = "Delivery state is required")
    @Size(max = 50, message = "Delivery state must not exceed 50 characters")
    private String deliveryState;

    @NotBlank(message = "Delivery zip code is required")
    @Pattern(regexp = "^[0-9]{5,10}$", message = "Delivery zip code must be 5-10 digits")
    private String deliveryZipCode;

    @NotBlank(message = "Delivery phone is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Delivery phone must be 10-15 digits")
    private String deliveryPhone;

    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.01", message = "Subtotal must be positive")
    @Digits(integer = 6, fraction = 2, message = "Subtotal must have maximum 6 integer digits and 2 fraction digits")
    private BigDecimal subtotal;

    @DecimalMin(value = "0.0", message = "Tip amount must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Tip amount must have maximum 6 integer digits and 2 fraction digits")
    private BigDecimal tipAmount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Size(max = 500, message = "Special instructions must not exceed 500 characters")
    private String specialInstructions;

    @Min(value = 10, message = "Estimated delivery time must be at least 10 minutes")
    @Max(value = 180, message = "Estimated delivery time must not exceed 180 minutes")
    private Integer estimatedDeliveryTime;
}