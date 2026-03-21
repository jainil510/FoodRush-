package com.foodrush.food_ordering_system.dto.request;

import com.foodrush.food_ordering_system.enums.CuisineType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.URL;

@Data
public class RestaurantRequest {

    @NotBlank(message = "Restaurant name is required")
    @Size(max = 100, message = "Restaurant name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be 10-15 digits")
    private String phone;

    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State must not exceed 50 characters")
    private String state;

    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = "^[0-9]{5,10}$", message = "Zip code must be 5-10 digits")
    private String zipCode;

    @NotBlank(message = "Country is required")
    @Size(max = 50, message = "Country must not exceed 50 characters")
    private String country;

    @NotNull(message = "Cuisine type is required")
    private CuisineType cuisineType;

    @NotNull(message = "Delivery fee is required")
    @DecimalMin(value = "0.0", message = "Delivery fee must be positive")
    @Digits(integer = 3, fraction = 2, message = "Delivery fee must have maximum 3 integer digits and 2 fraction digits")
    private BigDecimal deliveryFee;

    @NotNull(message = "Average delivery time is required")
    @Min(value = 10, message = "Average delivery time must be at least 10 minutes")
    @Max(value = 120, message = "Average delivery time must not exceed 120 minutes")
    private Integer averageDeliveryTime;

    @NotBlank(message = "Image URL is required")
    @URL(message = "Invalid image URL format")
    private String imageUrl;
}