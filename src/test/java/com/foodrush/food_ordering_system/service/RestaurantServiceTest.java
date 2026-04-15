package com.foodrush.food_ordering_system.service;

import com.foodrush.food_ordering_system.TestBase;
import com.foodrush.food_ordering_system.entity.Restaurant;
import com.foodrush.food_ordering_system.enums.CuisineType;
import com.foodrush.food_ordering_system.enums.RestaurantStatus;
import com.foodrush.food_ordering_system.exception.ResourceNotFoundException;
import com.foodrush.food_ordering_system.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest extends TestBase {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        testRestaurant = new Restaurant();
        testRestaurant.setId(1L);
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setEmail("restaurant@test.com");
        testRestaurant.setPhone("1234567890");
        testRestaurant.setAddress("123 Test St");
        testRestaurant.setCity("Test City");
        testRestaurant.setState("Test State");
        testRestaurant.setZipCode("12345");
        testRestaurant.setCountry("Test Country");
        testRestaurant.setCuisineType(CuisineType.INDIAN);
        testRestaurant.setDeliveryFee(new BigDecimal("5.00"));
        testRestaurant.setAverageDeliveryTime(30);
        testRestaurant.setImageUrl("http://example.com/image.jpg");
    }

    @Test
    void testCreateRestaurant_Success() {
        // Arrange
        when(restaurantRepository.existsByEmail("restaurant@test.com")).thenReturn(false);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        // Act
        Restaurant result = restaurantService.createRestaurant(testRestaurant);

        // Assert
        assertNotNull(result);
        assertEquals("Test Restaurant", result.getName());
        assertEquals("restaurant@test.com", result.getEmail());
        assertEquals(RestaurantStatus.PENDING, result.getStatus());
        assertFalse(result.getIsOpen());
        assertEquals(0.0, result.getRating());
        assertEquals(0, result.getTotalRatings());

        verify(restaurantRepository).existsByEmail("restaurant@test.com");
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void testCreateRestaurant_EmailAlreadyExists() {
        // Arrange
        when(restaurantRepository.existsByEmail("restaurant@test.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                restaurantService.createRestaurant(testRestaurant)
        );

        assertEquals("Restaurant with email restaurant@test.com already exists", exception.getMessage());
        verify(restaurantRepository).existsByEmail("restaurant@test.com");
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    @Test
    void testGetRestaurantById_Success() {
        // Arrange
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));

        // Act
        Restaurant result = restaurantService.getRestaurantById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testRestaurant, result);
        verify(restaurantRepository).findById(1L);
    }

    @Test
    void testGetRestaurantById_NotFound() {
        // Arrange
        when(restaurantRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                restaurantService.getRestaurantById(999L)
        );

        assertEquals("Restaurant not found with id: 999", exception.getMessage());
        verify(restaurantRepository).findById(999L);
    }

    @Test
    void testGetRestaurantByEmail_Success() {
        // Arrange
        when(restaurantRepository.findByEmail("restaurant@test.com")).thenReturn(Optional.of(testRestaurant));

        // Act
        Restaurant result = restaurantService.getRestaurantByEmail("restaurant@test.com");

        // Assert
        assertNotNull(result);
        assertEquals(testRestaurant, result);
        verify(restaurantRepository).findByEmail("restaurant@test.com");
    }

    @Test
    void testGetAllRestaurants() {
        // Arrange
        List<Restaurant> restaurants = Arrays.asList(testRestaurant);
        when(restaurantRepository.findAll()).thenReturn(restaurants);

        // Act
        List<Restaurant> result = restaurantService.getAllRestaurants();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRestaurant, result.get(0));
        verify(restaurantRepository).findAll();
    }

    @Test
    void testGetRestaurantsByStatus() {
        // Arrange
        List<Restaurant> restaurants = Arrays.asList(testRestaurant);
        when(restaurantRepository.findByStatus(RestaurantStatus.APPROVED)).thenReturn(restaurants);

        // Act
        List<Restaurant> result = restaurantService.getRestaurantsByStatus(RestaurantStatus.APPROVED);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(restaurantRepository).findByStatus(RestaurantStatus.APPROVED);
    }

    @Test
    void testGetRestaurantsByCuisineType() {
        // Arrange
        List<Restaurant> restaurants = Arrays.asList(testRestaurant);
        when(restaurantRepository.findByCuisineType(CuisineType.INDIAN)).thenReturn(restaurants);

        // Act
        List<Restaurant> result = restaurantService.getRestaurantsByCuisineType(CuisineType.INDIAN);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(restaurantRepository).findByCuisineType(CuisineType.INDIAN);
    }

    @Test
    void testApproveRestaurant() {
        // Arrange
        Restaurant pendingRestaurant = new Restaurant();
        pendingRestaurant.setId(1L);
        pendingRestaurant.setStatus(RestaurantStatus.PENDING);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(pendingRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(pendingRestaurant);

        // Act
        Restaurant result = restaurantService.approveRestaurant(1L);

        // Assert
        assertNotNull(result);
        assertEquals(RestaurantStatus.APPROVED, result.getStatus());
        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).save(pendingRestaurant);
    }

    @Test
    void testRejectRestaurant() {
        // Arrange
        Restaurant pendingRestaurant = new Restaurant();
        pendingRestaurant.setId(1L);
        pendingRestaurant.setStatus(RestaurantStatus.PENDING);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(pendingRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(pendingRestaurant);

        // Act
        Restaurant result = restaurantService.rejectRestaurant(1L);

        // Assert
        assertNotNull(result);
        assertEquals(RestaurantStatus.REJECTED, result.getStatus());
        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).save(pendingRestaurant);
    }

    @Test
    void testToggleRestaurantStatus_Open() {
        // Arrange
        Restaurant closedRestaurant = new Restaurant();
        closedRestaurant.setId(1L);
        closedRestaurant.setIsOpen(false);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(closedRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(closedRestaurant);

        // Act
        Restaurant result = restaurantService.toggleRestaurantStatus(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsOpen());
        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).save(closedRestaurant);
    }

    @Test
    void testToggleRestaurantStatus_Close() {
        // Arrange
        Restaurant openRestaurant = new Restaurant();
        openRestaurant.setId(1L);
        openRestaurant.setIsOpen(true);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(openRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(openRestaurant);

        // Act
        Restaurant result = restaurantService.toggleRestaurantStatus(1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.getIsOpen());
        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).save(openRestaurant);
    }

    @Test
    void testDeleteRestaurant() {
        // Arrange
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));
        doNothing().when(restaurantRepository).delete(any(Restaurant.class));

        // Act
        restaurantService.deleteRestaurant(1L);

        // Assert
        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).delete(testRestaurant);
    }

    @Test
    void testGetRestaurantCountByStatus() {
        // Arrange
        when(restaurantRepository.countByStatus(RestaurantStatus.APPROVED)).thenReturn(5L);

        // Act
        Long result = restaurantService.getRestaurantCountByStatus(RestaurantStatus.APPROVED);

        // Assert
        assertEquals(5L, result);
        verify(restaurantRepository).countByStatus(RestaurantStatus.APPROVED);
    }

    @Test
    void testGetTotalRestaurantsCount() {
        // Arrange
        when(restaurantRepository.count()).thenReturn(10L);

        // Act
        Long result = restaurantService.getTotalRestaurantsCount();

        // Assert
        assertEquals(10L, result);
        verify(restaurantRepository).count();
    }

    @Test
    void testGetRestaurantsCountByStatus() {
        // Arrange
        when(restaurantRepository.countByStatus(RestaurantStatus.PENDING)).thenReturn(3L);

        // Act
        Long result = restaurantService.getRestaurantsCountByStatus(RestaurantStatus.PENDING);

        // Assert
        assertEquals(3L, result);
        verify(restaurantRepository).countByStatus(RestaurantStatus.PENDING);
    }
}
