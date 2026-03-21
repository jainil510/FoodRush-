package com.foodrush.food_ordering_system.controller;

import com.foodrush.food_ordering_system.dto.request.RestaurantRequest;
import com.foodrush.food_ordering_system.dto.response.RestaurantResponse;
import com.foodrush.food_ordering_system.entity.Restaurant;
import com.foodrush.food_ordering_system.enums.CuisineType;
import com.foodrush.food_ordering_system.enums.RestaurantStatus;
import com.foodrush.food_ordering_system.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Slf4j
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody RestaurantRequest restaurantRequest) {
        try {
            log.info("Received request to create restaurant: {}", restaurantRequest.getName());

            Restaurant restaurant = convertToEntity(restaurantRequest);
            Restaurant createdRestaurant = restaurantService.createRestaurant(restaurant);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(RestaurantResponse.fromRestaurant(createdRestaurant));
        } catch (Exception e) {
            log.error("Error creating restaurant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(@PathVariable Long id,
                                                               @Valid @RequestBody RestaurantRequest restaurantRequest) {
        try {
            log.info("Received request to update restaurant with ID: {}", id);

            Restaurant restaurantDetails = convertToEntity(restaurantRequest);
            Restaurant updatedRestaurant = restaurantService.updateRestaurant(id, restaurantDetails);

            return ResponseEntity.ok(RestaurantResponse.fromRestaurant(updatedRestaurant));
        } catch (Exception e) {
            log.error("Error updating restaurant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable Long id) {
        try {
            log.info("Fetching restaurant with ID: {}", id);
            Restaurant restaurant = restaurantService.getRestaurantById(id);
            return ResponseEntity.ok(RestaurantResponse.fromRestaurant(restaurant));
        } catch (Exception e) {
            log.error("Error fetching restaurant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<RestaurantResponse> getRestaurantByEmail(@PathVariable String email) {
        try {
            log.info("Fetching restaurant with email: {}", email);
            Restaurant restaurant = restaurantService.getRestaurantByEmail(email);
            return ResponseEntity.ok(RestaurantResponse.fromRestaurant(restaurant));
        } catch (Exception e) {
            log.error("Error fetching restaurant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        try {
            log.info("Fetching all restaurants");
            List<Restaurant> restaurants = restaurantService.getAllRestaurants();
            List<RestaurantResponse> responses = restaurants.stream()
                    .map(RestaurantResponse::minimalFromRestaurant)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching restaurants: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByStatus(@PathVariable RestaurantStatus status) {
        try {
            log.info("Fetching restaurants with status: {}", status);
            List<Restaurant> restaurants = restaurantService.getRestaurantsByStatus(status);
            List<RestaurantResponse> responses = restaurants.stream()
                    .map(RestaurantResponse::minimalFromRestaurant)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching restaurants by status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cuisine/{cuisineType}")
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByCuisineType(@PathVariable CuisineType cuisineType) {
        try {
            log.info("Fetching restaurants with cuisine type: {}", cuisineType);
            List<Restaurant> restaurants = restaurantService.getRestaurantsByCuisineType(cuisineType);
            List<RestaurantResponse> responses = restaurants.stream()
                    .map(RestaurantResponse::minimalFromRestaurant)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching restaurants by cuisine type: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByCity(@PathVariable String city) {
        try {
            log.info("Fetching restaurants in city: {}", city);
            List<Restaurant> restaurants = restaurantService.getRestaurantsByCity(city);
            List<RestaurantResponse> responses = restaurants.stream()
                    .map(RestaurantResponse::minimalFromRestaurant)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching restaurants by city: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<RestaurantResponse>> getActiveRestaurants() {
        try {
            log.info("Fetching active restaurants");
            List<Restaurant> restaurants = restaurantService.getActiveRestaurants();
            List<RestaurantResponse> responses = restaurants.stream()
                    .map(RestaurantResponse::minimalFromRestaurant)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching active restaurants: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<RestaurantResponse>> searchRestaurants(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) CuisineType cuisineType) {
        try {
            log.info("Searching restaurants with name: {}, city: {}, cuisine: {}", name, city, cuisineType);
            List<Restaurant> restaurants = restaurantService.searchRestaurants(name, city, cuisineType);
            List<RestaurantResponse> responses = restaurants.stream()
                    .map(RestaurantResponse::minimalFromRestaurant)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error searching restaurants: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<RestaurantResponse>> getTopRatedRestaurants(
            @RequestParam(defaultValue = "4.0") Double minRating) {
        try {
            log.info("Fetching top rated restaurants with minimum rating: {}", minRating);
            List<Restaurant> restaurants = restaurantService.getTopRatedRestaurants(minRating);
            List<RestaurantResponse> responses = restaurants.stream()
                    .map(RestaurantResponse::minimalFromRestaurant)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching top rated restaurants: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<RestaurantResponse> approveRestaurant(@PathVariable Long id) {
        try {
            log.info("Approving restaurant with ID: {}", id);
            Restaurant restaurant = restaurantService.approveRestaurant(id);
            return ResponseEntity.ok(RestaurantResponse.fromRestaurant(restaurant));
        } catch (Exception e) {
            log.error("Error approving restaurant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<RestaurantResponse> rejectRestaurant(@PathVariable Long id) {
        try {
            log.info("Rejecting restaurant with ID: {}", id);
            Restaurant restaurant = restaurantService.rejectRestaurant(id);
            return ResponseEntity.ok(RestaurantResponse.fromRestaurant(restaurant));
        } catch (Exception e) {
            log.error("Error rejecting restaurant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<RestaurantResponse> toggleRestaurantStatus(@PathVariable Long id) {
        try {
            log.info("Toggling restaurant status with ID: {}", id);
            Restaurant restaurant = restaurantService.toggleRestaurantStatus(id);
            return ResponseEntity.ok(RestaurantResponse.fromRestaurant(restaurant));
        } catch (Exception e) {
            log.error("Error toggling restaurant status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        try {
            log.info("Deleting restaurant with ID: {}", id);
            restaurantService.deleteRestaurant(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting restaurant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/stats/count")
    public ResponseEntity<Long> getRestaurantCountByStatus(@RequestParam RestaurantStatus status) {
        try {
            log.info("Fetching restaurant count for status: {}", status);
            Long count = restaurantService.getRestaurantCountByStatus(status);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error fetching restaurant count: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Restaurant convertToEntity(RestaurantRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setEmail(request.getEmail());
        restaurant.setPhone(request.getPhone());
        restaurant.setAddress(request.getAddress());
        restaurant.setCity(request.getCity());
        restaurant.setState(request.getState());
        restaurant.setZipCode(request.getZipCode());
        restaurant.setCountry(request.getCountry());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setDeliveryFee(request.getDeliveryFee());
        restaurant.setAverageDeliveryTime(request.getAverageDeliveryTime());
        restaurant.setImageUrl(request.getImageUrl());
        return restaurant;
    }
}