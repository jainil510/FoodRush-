package com.foodrush.food_ordering_system.repository;

import com.foodrush.food_ordering_system.entity.Order;
import com.foodrush.food_ordering_system.enums.OrderStatus;
import com.foodrush.food_ordering_system.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    
    List<Order> findByUserId(Long userId);
    List<Order> findByRestaurantId(Long restaurantId);
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);
    List<Order> findByOrderStatus(OrderStatus status);
    List<Order> findByPaymentStatus(PaymentStatus status);

    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findOrdersByUserOrderByDate(@Param("userId") Long userId);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.orderStatus = :status ORDER BY o.createdAt DESC")
    List<Order> findOrdersByUserAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    Page<Order> findOrdersByUserWithPagination(@Param("userId") Long userId, Pageable pageable);

    
    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId ORDER BY o.createdAt DESC")
    List<Order> findOrdersByRestaurantOrderByDate(@Param("restaurantId") Long restaurantId);

    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId AND o.orderStatus = :status ORDER BY o.createdAt DESC")
    List<Order> findOrdersByRestaurantAndStatus(@Param("restaurantId") Long restaurantId, @Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId AND o.orderStatus IN :statuses ORDER BY o.createdAt DESC")
    List<Order> findOrdersByRestaurantAndStatuses(@Param("restaurantId") Long restaurantId, @Param("statuses") List<OrderStatus> statuses);

    
    @Query("SELECT o FROM Order o WHERE o.orderStatus IN :statuses ORDER BY o.createdAt ASC")
    List<Order> findActiveOrders(@Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT o FROM Order o WHERE o.orderStatus = :status ORDER BY o.createdAt ASC")
    List<Order> findOrdersByStatusOrderByDate(@Param("status") OrderStatus status);

    
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findUserOrdersByDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId AND o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findRestaurantOrdersByDateRange(@Param("restaurantId") Long restaurantId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    
    @Query("SELECT o FROM Order o WHERE o.paymentStatus = :paymentStatus ORDER BY o.createdAt DESC")
    List<Order> findOrdersByPaymentStatus(@Param("paymentStatus") PaymentStatus paymentStatus);

    @Query("SELECT o FROM Order o WHERE o.paymentMethod = :paymentMethod ORDER BY o.createdAt DESC")
    List<Order> findOrdersByPaymentMethod(@Param("paymentMethod") String paymentMethod);

    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status")
    Long countOrdersByStatus(@Param("status") OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.orderStatus = :status")
    Long countOrdersByRestaurantAndStatus(@Param("restaurantId") Long restaurantId, @Param("status") OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND o.orderStatus = :status")
    Long countOrdersByUserAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderStatus = :status AND o.paymentStatus = 'PAID'")
    Double calculateRevenueByStatus(@Param("status") OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.orderStatus = :status AND o.paymentStatus = 'PAID'")
    Double calculateRevenueByRestaurantAndStatus(@Param("restaurantId") Long restaurantId, @Param("status") OrderStatus status);

    
    @Query("SELECT o FROM Order o WHERE o.orderStatus = 'OUT_FOR_DELIVERY' ORDER BY o.createdAt ASC")
    List<Order> findOrdersOutForDelivery();

    @Query("SELECT o FROM Order o WHERE o.driverName IS NOT NULL ORDER BY o.createdAt DESC")
    List<Order> findOrdersWithAssignedDriver();

    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND (o.id LIKE %:searchTerm% OR o.restaurant.name LIKE %:searchTerm%) ORDER BY o.createdAt DESC")
    List<Order> searchUserOrders(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);

    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId AND (o.id LIKE %:searchTerm% OR o.user.firstName LIKE %:searchTerm% OR o.user.lastName LIKE %:searchTerm%) ORDER BY o.createdAt DESC")
    List<Order> searchRestaurantOrders(@Param("restaurantId") Long restaurantId, @Param("searchTerm") String searchTerm);

    
    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findRecentUserOrders(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId ORDER BY o.createdAt DESC")
    List<Order> findRecentRestaurantOrders(@Param("restaurantId") Long restaurantId, Pageable pageable);
}