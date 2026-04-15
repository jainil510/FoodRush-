package com.foodrush.food_ordering_system.service;

import com.foodrush.food_ordering_system.entity.Restaurant;
import com.foodrush.food_ordering_system.enums.CuisineType;
import com.foodrush.food_ordering_system.enums.RestaurantStatus;
import com.foodrush.food_ordering_system.exception.ResourceNotFoundException;
import com.foodrush.food_ordering_system.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    
    public Restaurant createRestaurant(Restaurant restaurant) {
        log.info("Creating new restaurant: {}", restaurant.getName());
        
       
        if (restaurantRepository.existsByEmail(restaurant.getEmail())) {
            throw new RuntimeException("Restaurant with email " + restaurant.getEmail() + " already exists");
        }
        
        
        restaurant.setStatus(RestaurantStatus.PENDING);
        restaurant.setIsOpen(false);
        restaurant.setRating(0.0);
        restaurant.setTotalRatings(0);
        
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant created successfully with ID: {}", savedRestaurant.getId());
        
        return savedRestaurant;
    }

    
    public Restaurant updateRestaurant(Long id, Restaurant restaurantDetails) {
        log.info("Updating restaurant with ID: {}", id);
        
        Restaurant existingRestaurant = getRestaurantById(id);
        
        
        existingRestaurant.setName(restaurantDetails.getName());
        existingRestaurant.setDescription(restaurantDetails.getDescription());
        existingRestaurant.setPhone(restaurantDetails.getPhone());
        existingRestaurant.setAddress(restaurantDetails.getAddress());
        existingRestaurant.setCity(restaurantDetails.getCity());
        existingRestaurant.setState(restaurantDetails.getState());
        existingRestaurant.setZipCode(restaurantDetails.getZipCode());
        existingRestaurant.setCountry(restaurantDetails.getCountry());
        existingRestaurant.setCuisineType(restaurantDetails.getCuisineType());
        existingRestaurant.setDeliveryFee(restaurantDetails.getDeliveryFee());
        existingRestaurant.setAverageDeliveryTime(restaurantDetails.getAverageDeliveryTime());
        existingRestaurant.setImageUrl(restaurantDetails.getImageUrl());
        
        Restaurant updatedRestaurant = restaurantRepository.save(existingRestaurant);
        log.info("Restaurant updated successfully: {}", updatedRestaurant.getName());
        
        return updatedRestaurant;
    }

    
    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    }

    public Restaurant getRestaurantByEmail(String email) {
        return restaurantRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with email: " + email));
    }

    public List<Restaurant> getAllRestaurants() {
        log.info("Fetching all restaurants");
        return restaurantRepository.findAll();
    }

    public List<Restaurant> getRestaurantsByStatus(RestaurantStatus status) {
        log.info("Fetching restaurants with status: {}", status);
        return restaurantRepository.findByStatus(status);
    }

    public List<Restaurant> getRestaurantsByCuisineType(CuisineType cuisineType) {
        log.info("Fetching restaurants with cuisine type: {}", cuisineType);
        return restaurantRepository.findByCuisineType(cuisineType);
    }

    public List<Restaurant> getRestaurantsByCity(String city) {
        log.info("Fetching restaurants in city: {}", city);
        return restaurantRepository.findByCityAndStatus(city, RestaurantStatus.APPROVED);
    }

    public List<Restaurant> getActiveRestaurants() {
        log.info("Fetching active restaurants");
        return restaurantRepository.findActiveRestaurants(RestaurantStatus.APPROVED);
    }

    
    public List<Restaurant> searchRestaurants(String name, String city, CuisineType cuisineType) {
        log.info("Searching restaurants with name: {}, city: {}, cuisine: {}", name, city, cuisineType);
        
        if (name != null && !name.trim().isEmpty()) {
            return restaurantRepository.findByNameContaining(name.trim(), RestaurantStatus.APPROVED);
        } else if (city != null && cuisineType != null) {
            return restaurantRepository.findByCityAndCuisineTypeAndStatus(city, cuisineType, RestaurantStatus.APPROVED);
        } else if (city != null) {
            return restaurantRepository.findByCityAndStatus(city, RestaurantStatus.APPROVED);
        } else if (cuisineType != null) {
            return restaurantRepository.findByCuisineType(cuisineType);
        } else {
            return getActiveRestaurants();
        }
    }

    public List<Restaurant> getTopRatedRestaurants(Double minRating) {
        log.info("Fetching top rated restaurants with minimum rating: {}", minRating);
        return restaurantRepository.findTopRatedRestaurants(minRating, RestaurantStatus.APPROVED);
    }

    
    public Restaurant approveRestaurant(Long id) {
        log.info("Approving restaurant with ID: {}", id);
        
        Restaurant restaurant = getRestaurantById(id);
        restaurant.setStatus(RestaurantStatus.APPROVED);
        
        Restaurant approvedRestaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant approved successfully: {}", approvedRestaurant.getName());
        
        return approvedRestaurant;
    }

    public Restaurant rejectRestaurant(Long id) {
        log.info("Rejecting restaurant with ID: {}", id);
        
        Restaurant restaurant = getRestaurantById(id);
        restaurant.setStatus(RestaurantStatus.REJECTED);
        
        Restaurant rejectedRestaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant rejected successfully: {}", rejectedRestaurant.getName());
        
        return rejectedRestaurant;
    }

    public Restaurant toggleRestaurantStatus(Long id) {
        log.info("Toggling restaurant status with ID: {}", id);
        
        Restaurant restaurant = getRestaurantById(id);
        restaurant.setIsOpen(!restaurant.getIsOpen());
        
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant status toggled: {} is now open: {}", updatedRestaurant.getName(), updatedRestaurant.getIsOpen());
        
        return updatedRestaurant;
    }

   
    public void deleteRestaurant(Long id) {
        log.info("Deleting restaurant with ID: {}", id);
        
        Restaurant restaurant = getRestaurantById(id);
        restaurantRepository.delete(restaurant);

        log.info("Restaurant deleted successfully: {}", restaurant.getName());
    }

    public Long getRestaurantCountByStatus(RestaurantStatus status) {
        return restaurantRepository.countByStatus(status);
    }

    public Long getTotalRestaurantsCount() {
        log.info("Getting total restaurants count");
        return restaurantRepository.count();
    }

    public Long getRestaurantsCountByStatus(RestaurantStatus status) {
        log.info("Getting restaurants count by status: {}", status);
        return restaurantRepository.countByStatus(status);
    }

    // Admin methods
    public List<Restaurant> getAllRestaurantsAdmin() {
        log.info("Fetching all restaurants");
        return restaurantRepository.findAll();
    }

    public List<Restaurant> getRestaurantsByStatusAdmin(RestaurantStatus status) {
        log.info("Fetching restaurants with status: {}", status);
        return restaurantRepository.findByStatus(status);
    }

    public Long getTotalRestaurantsCountAdmin() {
        log.info("Getting total restaurants count");
        return restaurantRepository.count();
    }
}