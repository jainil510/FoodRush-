package com.foodrush.food_ordering_system.dto.response;

import com.foodrush.food_ordering_system.enums.CuisineType;
import com.foodrush.food_ordering_system.enums.RestaurantStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RestaurantResponse {

    private Long id;
    private String name;
    private String description;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private CuisineType cuisineType;
    private RestaurantStatus status;
    private BigDecimal deliveryFee;
    private Integer averageDeliveryTime;
    private Double rating;
    private Integer totalRatings;
    private Boolean isOpen;
    private String imageUrl;
    private String createdAt;
    private String updatedAt;

    
    public RestaurantResponse(Long id, String name, String description, CuisineType cuisineType, 
                          String city, Double rating, Boolean isOpen, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cuisineType = cuisineType;
        this.city = city;
        this.rating = rating;
        this.isOpen = isOpen;
        this.imageUrl = imageUrl;
    }

    
    public static RestaurantResponse fromRestaurant(com.foodrush.food_ordering_system.entity.Restaurant restaurant) {
        return new RestaurantResponse(
            restaurant.getId(),
            restaurant.getName(),
            restaurant.getDescription(),
            restaurant.getEmail(),
            restaurant.getPhone(),
            restaurant.getAddress(),
            restaurant.getCity(),
            restaurant.getState(),
            restaurant.getZipCode(),
            restaurant.getCountry(),
            restaurant.getCuisineType(),
            restaurant.getStatus(),
            restaurant.getDeliveryFee(),
            restaurant.getAverageDeliveryTime(),
            restaurant.getRating(),
            restaurant.getTotalRatings(),
            restaurant.getIsOpen(),
            restaurant.getImageUrl(),
            restaurant.getCreatedAt().toString(),
            restaurant.getUpdatedAt().toString()
        );
    }

    
    public static RestaurantResponse minimalFromRestaurant(com.foodrush.food_ordering_system.entity.Restaurant restaurant) {
        return new RestaurantResponse(
            restaurant.getId(),
            restaurant.getName(),
            restaurant.getDescription(),
            restaurant.getCuisineType(),
            restaurant.getCity(),
            restaurant.getRating(),
            restaurant.getIsOpen(),
            restaurant.getImageUrl()
        );
    }
}