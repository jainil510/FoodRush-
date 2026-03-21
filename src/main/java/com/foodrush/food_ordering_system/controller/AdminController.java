package com.foodrush.food_ordering_system.controller;

import com.foodrush.food_ordering_system.entity.Restaurant;
import com.foodrush.food_ordering_system.entity.User;
import com.foodrush.food_ordering_system.enums.RestaurantStatus;
import com.foodrush.food_ordering_system.enums.Role;
import com.foodrush.food_ordering_system.service.RestaurantService;
import com.foodrush.food_ordering_system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final RestaurantService restaurantService;

    // User Management
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            log.info("Admin fetching all users");
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error fetching all users: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable Long userId, @RequestBody Role newRole) {
        try {
            log.info("Admin updating user {} role to: {}", userId, newRole);
            User updatedUser = userService.updateUserRole(userId, newRole);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Error updating user role: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<User> toggleUserStatus(@PathVariable Long userId) {
        try {
            log.info("Admin toggling status for user: {}", userId);
            User updatedUser = userService.toggleUserStatus(userId);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Error toggling user status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Restaurant Management
    @GetMapping("/restaurants")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        try {
            log.info("Admin fetching all restaurants");
            List<Restaurant> restaurants = restaurantService.getAllRestaurants();
            return ResponseEntity.ok(restaurants);
        } catch (Exception e) {
            log.error("Error fetching all restaurants: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/restaurants/pending")
    public ResponseEntity<List<Restaurant>> getPendingRestaurants() {
        try {
            log.info("Admin fetching pending restaurants");
            List<Restaurant> restaurants = restaurantService.getRestaurantsByStatus(RestaurantStatus.PENDING);
            return ResponseEntity.ok(restaurants);
        } catch (Exception e) {
            log.error("Error fetching pending restaurants: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/restaurants/{restaurantId}/approve")
    public ResponseEntity<Restaurant> approveRestaurant(@PathVariable Long restaurantId) {
        try {
            log.info("Admin approving restaurant: {}", restaurantId);
            Restaurant approvedRestaurant = restaurantService.approveRestaurant(restaurantId);
            return ResponseEntity.ok(approvedRestaurant);
        } catch (Exception e) {
            log.error("Error approving restaurant: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/restaurants/{restaurantId}/reject")
    public ResponseEntity<Restaurant> rejectRestaurant(@PathVariable Long restaurantId) {
        try {
            log.info("Admin rejecting restaurant: {}", restaurantId);
            Restaurant rejectedRestaurant = restaurantService.rejectRestaurant(restaurantId);
            return ResponseEntity.ok(rejectedRestaurant);
        } catch (Exception e) {
            log.error("Error rejecting restaurant: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Statistics
    @GetMapping("/stats/users")
    public ResponseEntity<Long> getTotalUsers() {
        try {
            log.info("Admin fetching total users count");
            Long count = userService.getTotalUsersCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error fetching total users count: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/stats/restaurants")
    public ResponseEntity<Long> getTotalRestaurants() {
        try {
            log.info("Admin fetching total restaurants count");
            Long count = restaurantService.getTotalRestaurantsCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error fetching total restaurants count: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/stats/restaurants/pending")
    public ResponseEntity<Long> getPendingRestaurantsCount() {
        try {
            log.info("Admin fetching pending restaurants count");
            Long count = restaurantService.getRestaurantsCountByStatus(RestaurantStatus.PENDING);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error fetching pending restaurants count: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
