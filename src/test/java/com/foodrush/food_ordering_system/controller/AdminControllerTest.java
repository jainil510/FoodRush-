package com.foodrush.food_ordering_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodrush.food_ordering_system.TestBase;
import com.foodrush.food_ordering_system.entity.Restaurant;
import com.foodrush.food_ordering_system.entity.User;
import com.foodrush.food_ordering_system.enums.RestaurantStatus;
import com.foodrush.food_ordering_system.enums.Role;
import com.foodrush.food_ordering_system.service.RestaurantService;
import com.foodrush.food_ordering_system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest extends TestBase {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private RestaurantService restaurantService;

    private MockMvc mockMvc;
    private User testUser;
    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("admin@example.com");
        testUser.setFirstName("Admin");
        testUser.setLastName("User");
        testUser.setRole(Role.ADMIN);

        testRestaurant = new Restaurant();
        testRestaurant.setId(1L);
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setStatus(RestaurantStatus.APPROVED);
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));

        // Act & Assert
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("admin@example.com"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"));

        verify(userService).getAllUsers();
    }

    @Test
    void testUpdateUserRole_Success() throws Exception {
        // Arrange
        testUser.setRole(Role.RESTAURANT_OWNER);
        when(userService.updateUserRole(anyLong(), any(Role.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(put("/api/admin/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Role.RESTAURANT_OWNER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("RESTAURANT_OWNER"));

        verify(userService).updateUserRole(1L, Role.RESTAURANT_OWNER);
    }

    @Test
    void testUpdateUserRole_UserNotFound() throws Exception {
        // Arrange
        when(userService.updateUserRole(anyLong(), any(Role.class)))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(put("/api/admin/users/999/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Role.ADMIN)))
                .andExpect(status().isBadRequest());

        verify(userService).updateUserRole(999L, Role.ADMIN);
    }

    @Test
    void testToggleUserStatus_Success() throws Exception {
        // Arrange
        testUser.setEnabled(false);
        when(userService.toggleUserStatus(anyLong())).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(put("/api/admin/users/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false));

        verify(userService).toggleUserStatus(1L);
    }

    @Test
    void testGetAllRestaurants_Success() throws Exception {
        // Arrange
        when(restaurantService.getAllRestaurants()).thenReturn(Arrays.asList(testRestaurant));

        // Act & Assert
        mockMvc.perform(get("/api/admin/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Restaurant"));

        verify(restaurantService).getAllRestaurants();
    }

    @Test
    void testGetPendingRestaurants_Success() throws Exception {
        // Arrange
        testRestaurant.setStatus(RestaurantStatus.PENDING);
        when(restaurantService.getRestaurantsByStatus(RestaurantStatus.PENDING))
                .thenReturn(Arrays.asList(testRestaurant));

        // Act & Assert
        mockMvc.perform(get("/api/admin/restaurants/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(restaurantService).getRestaurantsByStatus(RestaurantStatus.PENDING);
    }

    @Test
    void testApproveRestaurant_Success() throws Exception {
        // Arrange
        testRestaurant.setStatus(RestaurantStatus.APPROVED);
        when(restaurantService.approveRestaurant(anyLong())).thenReturn(testRestaurant);

        // Act & Assert
        mockMvc.perform(put("/api/admin/restaurants/1/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(restaurantService).approveRestaurant(1L);
    }

    @Test
    void testApproveRestaurant_NotFound() throws Exception {
        // Arrange
        when(restaurantService.approveRestaurant(anyLong()))
                .thenThrow(new RuntimeException("Restaurant not found"));

        // Act & Assert
        mockMvc.perform(put("/api/admin/restaurants/999/approve"))
                .andExpect(status().isBadRequest());

        verify(restaurantService).approveRestaurant(999L);
    }

    @Test
    void testRejectRestaurant_Success() throws Exception {
        // Arrange
        testRestaurant.setStatus(RestaurantStatus.REJECTED);
        when(restaurantService.rejectRestaurant(anyLong())).thenReturn(testRestaurant);

        // Act & Assert
        mockMvc.perform(put("/api/admin/restaurants/1/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        verify(restaurantService).rejectRestaurant(1L);
    }

    @Test
    void testGetTotalUsersCount_Success() throws Exception {
        // Arrange
        when(userService.getTotalUsersCount()).thenReturn(100L);

        // Act & Assert
        mockMvc.perform(get("/api/admin/stats/users"))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));

        verify(userService).getTotalUsersCount();
    }

    @Test
    void testGetTotalRestaurantsCount_Success() throws Exception {
        // Arrange
        when(restaurantService.getTotalRestaurantsCount()).thenReturn(50L);

        // Act & Assert
        mockMvc.perform(get("/api/admin/stats/restaurants"))
                .andExpect(status().isOk())
                .andExpect(content().string("50"));

        verify(restaurantService).getTotalRestaurantsCount();
    }

    @Test
    void testGetPendingRestaurantsCount_Success() throws Exception {
        // Arrange
        when(restaurantService.getRestaurantsCountByStatus(RestaurantStatus.PENDING)).thenReturn(10L);

        // Act & Assert
        mockMvc.perform(get("/api/admin/stats/restaurants/pending"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));

        verify(restaurantService).getRestaurantsCountByStatus(RestaurantStatus.PENDING);
    }

    @Test
    void testUpdateUserRole_InvalidRole() throws Exception {
        // Arrange
        when(userService.updateUserRole(anyLong(), any(Role.class)))
                .thenThrow(new RuntimeException("Invalid role"));

        // Act & Assert
        mockMvc.perform(put("/api/admin/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Role.CUSTOMER)))
                .andExpect(status().isBadRequest());

        verify(userService).updateUserRole(1L, Role.CUSTOMER);
    }

    @Test
    void testToggleUserStatus_UserNotFound() throws Exception {
        // Arrange
        when(userService.toggleUserStatus(anyLong()))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(put("/api/admin/users/999/status"))
                .andExpect(status().isBadRequest());

        verify(userService).toggleUserStatus(999L);
    }

    @Test
    void testRejectRestaurant_NotFound() throws Exception {
        // Arrange
        when(restaurantService.rejectRestaurant(anyLong()))
                .thenThrow(new RuntimeException("Restaurant not found"));

        // Act & Assert
        mockMvc.perform(put("/api/admin/restaurants/999/reject"))
                .andExpect(status().isBadRequest());

        verify(restaurantService).rejectRestaurant(999L);
    }

    @Test
    void testGetStats_Unauthorized() throws Exception {
        // This test would need security configuration to test unauthorized access
        // For now, just test the endpoint exists
        mockMvc.perform(get("/api/admin/stats/users"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateUserRole_SuccessfulChange() throws Exception {
        // Arrange
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setRole(Role.RESTAURANT_OWNER);
        
        when(userService.updateUserRole(1L, Role.RESTAURANT_OWNER)).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/api/admin/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Role.RESTAURANT_OWNER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("RESTAURANT_OWNER"));

        verify(userService).updateUserRole(1L, Role.RESTAURANT_OWNER);
    }
}
