package com.foodrush.food_ordering_system.repository;

import com.foodrush.food_ordering_system.entity.Restaurant;
import com.foodrush.food_ordering_system.enums.CuisineType;
import com.foodrush.food_ordering_system.enums.RestaurantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    
    Optional<Restaurant> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Restaurant> findByStatus(RestaurantStatus status);
    List<Restaurant> findByCuisineType(CuisineType cuisineType);
    List<Restaurant> findByCityAndStatus(String city, RestaurantStatus status);
    List<Restaurant> findByIsOpenTrue();

    
    @Query("SELECT r FROM Restaurant r WHERE r.status = :status AND r.isOpen = true")
    List<Restaurant> findActiveRestaurants(@Param("status") RestaurantStatus status);

    @Query("SELECT r FROM Restaurant r WHERE r.city = :city AND r.cuisineType = :cuisineType AND r.status = :status")
    List<Restaurant> findByCityAndCuisineTypeAndStatus(
            @Param("city") String city,
            @Param("cuisineType") CuisineType cuisineType,
            @Param("status") RestaurantStatus status
    );

    @Query("SELECT r FROM Restaurant r WHERE r.rating >= :minRating AND r.status = :status ORDER BY r.rating DESC")
    List<Restaurant> findTopRatedRestaurants(@Param("minRating") Double minRating, @Param("status") RestaurantStatus status);

    @Query("SELECT r FROM Restaurant r WHERE r.name LIKE %:name% AND r.status = :status")
    List<Restaurant> findByNameContaining(@Param("name") String name, @Param("status") RestaurantStatus status);

    @Query("SELECT COUNT(r) FROM Restaurant r WHERE r.status = :status")
    Long countByStatus(@Param("status") RestaurantStatus status);
}