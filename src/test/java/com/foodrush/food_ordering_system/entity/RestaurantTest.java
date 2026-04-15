package com.foodrush.food_ordering_system.entity;

import com.foodrush.food_ordering_system.TestBase;
import com.foodrush.food_ordering_system.enums.CuisineType;
import com.foodrush.food_ordering_system.enums.RestaurantStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest extends TestBase {

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
    }

    @Test
    void testRestaurantCreation() {
        restaurant.setName("Test Restaurant");
        restaurant.setEmail("restaurant@test.com");
        restaurant.setPhone("1234567890");
        restaurant.setAddress("123 Test St");
        restaurant.setCity("Test City");
        restaurant.setState("Test State");
        restaurant.setZipCode("12345");
        restaurant.setCountry("Test Country");
        restaurant.setCuisineType(CuisineType.INDIAN);
        restaurant.setDeliveryFee(new BigDecimal("5.00"));
        restaurant.setAverageDeliveryTime(30);
        restaurant.setImageUrl("http://example.com/image.jpg");

        assertNotNull(restaurant);
        assertEquals("Test Restaurant", restaurant.getName());
        assertEquals("restaurant@test.com", restaurant.getEmail());
        assertEquals(CuisineType.INDIAN, restaurant.getCuisineType());
        assertEquals("123 Test St", restaurant.getAddress());
        assertEquals("Test City", restaurant.getCity());
    }

    @Test
    void testRestaurantPrePersist() {
        // Test @PrePersist method
        restaurant.onCreate();

        assertEquals(RestaurantStatus.PENDING, restaurant.getStatus());
        assertEquals(0.0, restaurant.getRating());
        assertEquals(0, restaurant.getTotalRatings());
        assertFalse(restaurant.getIsOpen());
        assertNotNull(restaurant.getCreatedAt());
        assertNotNull(restaurant.getUpdatedAt());
    }

    @Test
    void testRestaurantPreUpdate() {
        // Set initial values
        restaurant.setRating(4.5);
        restaurant.setTotalRatings(100);
        restaurant.setIsOpen(true);

        // Test @PreUpdate method
        restaurant.onUpdate();

        assertNotNull(restaurant.getUpdatedAt());
        // Rating and other fields should remain unchanged
        assertEquals(4.5, restaurant.getRating());
        assertEquals(100, restaurant.getTotalRatings());
        assertTrue(restaurant.getIsOpen());
    }

    @Test
    void testRestaurantStatusTransitions() {
        restaurant.setStatus(RestaurantStatus.PENDING);
        assertEquals(RestaurantStatus.PENDING, restaurant.getStatus());

        restaurant.setStatus(RestaurantStatus.APPROVED);
        assertEquals(RestaurantStatus.APPROVED, restaurant.getStatus());

        restaurant.setStatus(RestaurantStatus.REJECTED);
        assertEquals(RestaurantStatus.REJECTED, restaurant.getStatus());
    }

    @Test
    void testRestaurantRatingSystem() {
        restaurant.setRating(4.5);
        restaurant.setTotalRatings(100);

        assertEquals(4.5, restaurant.getRating());
        assertEquals(100, restaurant.getTotalRatings());

        // Test rating update
        restaurant.setRating(4.7);
        restaurant.setTotalRatings(101);

        assertEquals(4.7, restaurant.getRating());
        assertEquals(101, restaurant.getTotalRatings());
    }

    @Test
    void testRestaurantToggleStatus() {
        restaurant.setIsOpen(false);
        assertFalse(restaurant.getIsOpen());

        restaurant.setIsOpen(true);
        assertTrue(restaurant.getIsOpen());
    }

    @Test
    void testRestaurantDeliveryFee() {
        BigDecimal deliveryFee = new BigDecimal("5.99");
        restaurant.setDeliveryFee(deliveryFee);

        assertEquals(deliveryFee, restaurant.getDeliveryFee());
        assertEquals(2, restaurant.getDeliveryFee().scale());
    }

    @Test
    void testRestaurantDeliveryTime() {
        restaurant.setAverageDeliveryTime(45);
        assertEquals(45, restaurant.getAverageDeliveryTime());

        restaurant.setAverageDeliveryTime(60);
        assertEquals(60, restaurant.getAverageDeliveryTime());
    }
}
