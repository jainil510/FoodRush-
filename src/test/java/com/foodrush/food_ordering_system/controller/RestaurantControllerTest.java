package com.foodrush.food_ordering_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodrush.food_ordering_system.TestBase;
import com.foodrush.food_ordering_system.dto.request.RestaurantRequest;
import com.foodrush.food_ordering_system.dto.response.RestaurantResponse;
import com.foodrush.food_ordering_system.entity.Restaurant;
import com.foodrush.food_ordering_system.enums.CuisineType;
import com.foodrush.food_ordering_system.enums.RestaurantStatus;
import com.foodrush.food_ordering_system.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RestaurantControllerTest extends TestBase {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestaurantService restaurantService;

    private MockMvc mockMvc;
    private RestaurantRequest testRestaurantRequest;
    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        testRestaurantRequest = new RestaurantRequest();
        testRestaurantRequest.setName("Test Restaurant");
        testRestaurantRequest.setDescription("Test Description");
        testRestaurantRequest.setEmail("restaurant@test.com");
        testRestaurantRequest.setPhone("1234567890");
        testRestaurantRequest.setAddress("123 Test St");
        testRestaurantRequest.setCity("Test City");
        testRestaurantRequest.setState("Test State");
        testRestaurantRequest.setZipCode("12345");
        testRestaurantRequest.setCountry("Test Country");
        testRestaurantRequest.setCuisineType(CuisineType.INDIAN);
        testRestaurantRequest.setDeliveryFee(new BigDecimal("5.00"));
        testRestaurantRequest.setAverageDeliveryTime(30);
        testRestaurantRequest.setImageUrl("http://example.com/image.jpg");

        testRestaurant = new Restaurant();
        testRestaurant.setId(1L);
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setEmail("restaurant@test.com");
        testRestaurant.setCuisineType(CuisineType.INDIAN);
        testRestaurant.setStatus(RestaurantStatus.APPROVED);
    }

    @Test
    void testCreateRestaurant_Success() throws Exception {
        // Arrange
        when(restaurantService.createRestaurant(any(Restaurant.class))).thenReturn(testRestaurant);

        // Act & Assert
        mockMvc.perform(post("/api/restaurants/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRestaurantRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Restaurant"))
                .andExpect(jsonPath("$.email").value("restaurant@test.com"))
                .andExpect(jsonPath("$.cuisineType").value("INDIAN"));

        verify(restaurantService).createRestaurant(any(Restaurant.class));
    }

    @Test
    void testCreateRestaurant_InvalidEmail() throws Exception {
        // Arrange
        testRestaurantRequest.setEmail("invalid-email");

        // Act & Assert
        mockMvc.perform(post("/api/restaurants/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRestaurantRequest)))
                .andExpect(status().isBadRequest());

        verify(restaurantService, never()).createRestaurant(any(Restaurant.class));
    }

    @Test
    void testCreateRestaurant_MissingRequiredFields() throws Exception {
        // Arrange
        RestaurantRequest invalidRequest = new RestaurantRequest();
        // Missing all required fields

        // Act & Assert
        mockMvc.perform(post("/api/restaurants/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(restaurantService, never()).createRestaurant(any(Restaurant.class));
    }

    @Test
    void testGetRestaurantById_Success() throws Exception {
        // Arrange
        when(restaurantService.getRestaurantById(1L)).thenReturn(testRestaurant);

        // Act & Assert
        mockMvc.perform(get("/api/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Restaurant"))
                .andExpect(jsonPath("$.email").value("restaurant@test.com"));

        verify(restaurantService).getRestaurantById(1L);
    }

    @Test
    void testGetRestaurantById_NotFound() throws Exception {
        // Arrange
        when(restaurantService.getRestaurantById(999L))
                .thenThrow(new RuntimeException("Restaurant not found"));

        // Act & Assert
        mockMvc.perform(get("/api/restaurants/999"))
                .andExpect(status().isNotFound());

        verify(restaurantService).getRestaurantById(999L);
    }

    @Test
    void testGetAllRestaurants_Success() throws Exception {
        // Arrange
        when(restaurantService.getAllRestaurants())
                .thenReturn(java.util.Arrays.asList(testRestaurant));

        // Act & Assert
        mockMvc.perform(get("/api/restaurants/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Restaurant"));

        verify(restaurantService).getAllRestaurants();
    }

    @Test
    void testSearchRestaurants_ByName() throws Exception {
        // Arrange
        when(restaurantService.searchRestaurants("Test", null, null))
                .thenReturn(java.util.Arrays.asList(testRestaurant));

        // Act & Assert
        mockMvc.perform(get("/api/restaurants/search?name=Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Restaurant"));

        verify(restaurantService).searchRestaurants("Test", null, null);
    }

    @Test
    void testSearchRestaurants_ByCity() throws Exception {
        // Arrange
        when(restaurantService.searchRestaurants(null, "Test City", null))
                .thenReturn(java.util.Arrays.asList(testRestaurant));

        // Act & Assert
        mockMvc.perform(get("/api/restaurants/search?city=Test City"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Test City"));

        verify(restaurantService).searchRestaurants(null, "Test City", null);
    }

    @Test
    void testSearchRestaurants_ByCuisine() throws Exception {
        // Arrange
        when(restaurantService.searchRestaurants(null, null, CuisineType.INDIAN))
                .thenReturn(java.util.Arrays.asList(testRestaurant));

        // Act & Assert
        mockMvc.perform(get("/api/restaurants/search?cuisineType=INDIAN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cuisineType").value("INDIAN"));

        verify(restaurantService).searchRestaurants(null, null, CuisineType.INDIAN);
    }

    @Test
    void testGetRestaurantsByCity_Success() throws Exception {
        // Arrange
        when(restaurantService.getRestaurantsByCity("Test City"))
                .thenReturn(java.util.Arrays.asList(testRestaurant));

        // Act & Assert
        mockMvc.perform(get("/api/restaurants/city/Test City"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Test City"));

        verify(restaurantService).getRestaurantsByCity("Test City");
    }

    @Test
    void testGetRestaurantsByCuisine_Success() throws Exception {
        // Arrange
        when(restaurantService.getRestaurantsByCuisineType(CuisineType.INDIAN))
                .thenReturn(java.util.Arrays.asList(testRestaurant));

        // Act & Assert
        mockMvc.perform(get("/api/restaurants/cuisine/INDIAN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cuisineType").value("INDIAN"));

        verify(restaurantService).getRestaurantsByCuisineType(CuisineType.INDIAN);
    }

    @Test
    void testUpdateRestaurant_Success() throws Exception {
        // Arrange
        when(restaurantService.updateRestaurant(anyLong(), any(Restaurant.class)))
                .thenReturn(testRestaurant);

        // Act & Assert
        mockMvc.perform(put("/api/restaurants/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRestaurantRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Restaurant"));

        verify(restaurantService).updateRestaurant(1L, any(Restaurant.class));
    }

    @Test
    void testUpdateRestaurant_NotFound() throws Exception {
        // Arrange
        when(restaurantService.updateRestaurant(anyLong(), any(Restaurant.class)))
                .thenThrow(new RuntimeException("Restaurant not found"));

        // Act & Assert
        mockMvc.perform(put("/api/restaurants/update/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRestaurantRequest)))
                .andExpect(status().isNotFound());

        verify(restaurantService).updateRestaurant(999L, any(Restaurant.class));
    }

    @Test
    void testApproveRestaurant_Success() throws Exception {
        // Arrange
        testRestaurant.setStatus(RestaurantStatus.APPROVED);
        when(restaurantService.approveRestaurant(1L)).thenReturn(testRestaurant);

        // Act & Assert
        mockMvc.perform(put("/api/restaurants/1/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(restaurantService).approveRestaurant(1L);
    }

    @Test
    void testRejectRestaurant_Success() throws Exception {
        // Arrange
        testRestaurant.setStatus(RestaurantStatus.REJECTED);
        when(restaurantService.rejectRestaurant(1L)).thenReturn(testRestaurant);

        // Act & Assert
        mockMvc.perform(put("/api/restaurants/1/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        verify(restaurantService).rejectRestaurant(1L);
    }

    @Test
    void testToggleRestaurantStatus_Success() throws Exception {
        // Arrange
        testRestaurant.setIsOpen(true);
        when(restaurantService.toggleRestaurantStatus(1L)).thenReturn(testRestaurant);

        // Act & Assert
        mockMvc.perform(put("/api/restaurants/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOpen").value(true));

        verify(restaurantService).toggleRestaurantStatus(1L);
    }

    @Test
    void testDeleteRestaurant_Success() throws Exception {
        // Arrange
        doNothing().when(restaurantService).deleteRestaurant(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/restaurants/1"))
                .andExpect(status().isNoContent());

        verify(restaurantService).deleteRestaurant(1L);
    }

    @Test
    void testDeleteRestaurant_NotFound() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Restaurant not found"))
                .when(restaurantService).deleteRestaurant(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/restaurants/999"))
                .andExpect(status().isNotFound());

        verify(restaurantService).deleteRestaurant(999L);
    }

    @Test
    void testCreateRestaurant_InvalidDeliveryFee() throws Exception {
        // Arrange
        testRestaurantRequest.setDeliveryFee(new BigDecimal("-5.00")); // Negative fee

        // Act & Assert
        mockMvc.perform(post("/api/restaurants/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRestaurantRequest)))
                .andExpected(status().isBadRequest());

        verify(restaurantService, never()).createRestaurant(any(Restaurant.class));
    }

    @Test
    void testCreateRestaurant_InvalidDeliveryTime() throws Exception {
        // Arrange
        testRestaurantRequest.setAverageDeliveryTime(5); // Too short

        // Act & Assert
        mockMvc.perform(post("/api/restaurants/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRestaurantRequest)))
                .andExpect(status().isBadRequest());

        verify(restaurantService, never()).createRestaurant(any(Restaurant.class));
    }
}
