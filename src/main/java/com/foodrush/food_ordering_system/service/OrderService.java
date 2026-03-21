package com.foodrush.food_ordering_system.service;

import com.foodrush.food_ordering_system.entity.Order;
import com.foodrush.food_ordering_system.entity.Restaurant;
import com.foodrush.food_ordering_system.entity.User;
import com.foodrush.food_ordering_system.enums.OrderStatus;
import com.foodrush.food_ordering_system.enums.PaymentStatus;
import com.foodrush.food_ordering_system.exception.ResourceNotFoundException;
import com.foodrush.food_ordering_system.repository.OrderRepository;
import com.foodrush.food_ordering_system.repository.RestaurantRepository;
import com.foodrush.food_ordering_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public Order createOrder(Order order) {
        log.info("Creating new order for user: {}, restaurant: {}",
                order.getUser().getId(), order.getRestaurant().getId());

        User user = userRepository.findById(order.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Restaurant restaurant = restaurantRepository.findById(order.getRestaurant().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        if (!restaurant.getIsOpen()) {
            throw new RuntimeException("Restaurant is currently closed");
        }
        if (restaurant.getStatus() != com.foodrush.food_ordering_system.enums.RestaurantStatus.APPROVED) {
            throw new RuntimeException("Restaurant is not approved for orders");
        }

        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        if (order.getSubtotal() != null) {
            calculateOrderTotals(order);
        }

        if (order.getEstimatedDeliveryTime() == null) {
            order.setEstimatedDeliveryTime(restaurant.getAverageDeliveryTime());
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());

        return savedOrder;
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    public Order getOrderByIdAndUser(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public List<Order> getOrdersByUser(Long userId) {
        log.info("Fetching orders for user: {}", userId);
        return orderRepository.findOrdersByUserOrderByDate(userId);
    }

    public Page<Order> getOrdersByUserWithPagination(Long userId, Pageable pageable) {
        log.info("Fetching orders for user: {} with pagination", userId);
        return orderRepository.findOrdersByUserWithPagination(userId, pageable);
    }

    public List<Order> getOrdersByUserAndStatus(Long userId, OrderStatus status) {
        log.info("Fetching orders for user: {} with status: {}", userId, status);
        return orderRepository.findOrdersByUserAndStatus(userId, status);
    }

    public List<Order> getUserOrdersByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching orders for user: {} from {} to {}", userId, startDate, endDate);
        return orderRepository.findUserOrdersByDateRange(userId, startDate, endDate);
    }

    public List<Order> searchUserOrders(Long userId, String searchTerm) {
        log.info("Searching orders for user: {} with term: {}", userId, searchTerm);
        return orderRepository.searchUserOrders(userId, searchTerm);
    }

    public List<Order> getOrdersByRestaurant(Long restaurantId) {
        log.info("Fetching orders for restaurant: {}", restaurantId);
        return orderRepository.findOrdersByRestaurantOrderByDate(restaurantId);
    }

    public List<Order> getOrdersByRestaurantAndStatus(Long restaurantId, OrderStatus status) {
        log.info("Fetching orders for restaurant: {} with status: {}", restaurantId, status);
        return orderRepository.findOrdersByRestaurantAndStatus(restaurantId, status);
    }

    public List<Order> getActiveRestaurantOrders(Long restaurantId) {
        log.info("Fetching active orders for restaurant: {}", restaurantId);
        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.PENDING, OrderStatus.CONFIRMED,
                OrderStatus.PREPARING, OrderStatus.READY_FOR_PICKUP
        );
        return orderRepository.findOrdersByRestaurantAndStatuses(restaurantId, activeStatuses);
    }

    public List<Order> getRestaurantOrdersByDateRange(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching orders for restaurant: {} from {} to {}", restaurantId, startDate, endDate);
        return orderRepository.findRestaurantOrdersByDateRange(restaurantId, startDate, endDate);
    }

    public List<Order> searchRestaurantOrders(Long restaurantId, String searchTerm) {
        log.info("Searching orders for restaurant: {} with term: {}", restaurantId, searchTerm);
        return orderRepository.searchRestaurantOrders(restaurantId, searchTerm);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Updating order {} status to: {}", orderId, newStatus);

        Order order = getOrderById(orderId);
        validateStatusTransition(order.getOrderStatus(), newStatus);
        order.setOrderStatus(newStatus);

        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} status updated to: {}", orderId, newStatus);

        return updatedOrder;
    }

    public Order confirmOrder(Long orderId) {
        log.info("Confirming order: {}", orderId);
        return updateOrderStatus(orderId, OrderStatus.CONFIRMED);
    }

    public Order startPreparingOrder(Long orderId) {
        log.info("Starting preparation for order: {}", orderId);
        return updateOrderStatus(orderId, OrderStatus.PREPARING);
    }

    public Order markOrderReadyForPickup(Long orderId) {
        log.info("Marking order {} as ready for pickup", orderId);
        return updateOrderStatus(orderId, OrderStatus.READY_FOR_PICKUP);
    }

    public Order cancelOrder(Long orderId, String reason) {
        log.info("Cancelling order: {} with reason: {}", orderId, reason);

        Order order = getOrderById(orderId);

        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel delivered order");
        }
        if (order.getOrderStatus() == OrderStatus.OUT_FOR_DELIVERY) {
            throw new RuntimeException("Cannot cancel order that is out for delivery");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    public Order updatePaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        log.info("Updating payment status for order {} to: {}", orderId, paymentStatus);

        Order order = getOrderById(orderId);
        order.setPaymentStatus(paymentStatus);

        Order updatedOrder = orderRepository.save(order);

        if (paymentStatus == PaymentStatus.PAID && order.getOrderStatus() == OrderStatus.PENDING) {
            updatedOrder = confirmOrder(orderId);
        }

        return updatedOrder;
    }

    public Order assignDriver(Long orderId, String driverName, String driverPhone) {
        log.info("Assigning driver {} to order: {}", driverName, orderId);

        Order order = getOrderById(orderId);
        order.setDriverName(driverName);
        order.setDriverPhone(driverPhone);

        return orderRepository.save(order);
    }

    public Order markOrderOutForDelivery(Long orderId) {
        log.info("Marking order {} as out for delivery", orderId);
        return updateOrderStatus(orderId, OrderStatus.OUT_FOR_DELIVERY);
    }

    public Order markOrderDelivered(Long orderId, Integer actualDeliveryTime) {
        log.info("Marking order {} as delivered", orderId);

        Order order = getOrderById(orderId);
        order.setOrderStatus(OrderStatus.DELIVERED);
        order.setActualDeliveryTime(actualDeliveryTime);

        Order deliveredOrder = orderRepository.save(order);
        log.info("Order {} delivered successfully", orderId);

        return deliveredOrder;
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        log.info("Fetching orders with status: {}", status);
        return orderRepository.findByOrderStatus(status);
    }

    public List<Order> getActiveOrders() {
        log.info("Fetching active orders");
        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.PENDING, OrderStatus.CONFIRMED,
                OrderStatus.PREPARING, OrderStatus.READY_FOR_PICKUP,
                OrderStatus.OUT_FOR_DELIVERY
        );
        return orderRepository.findActiveOrders(activeStatuses);
    }

    public List<Order> getOrdersOutForDelivery() {
        log.info("Fetching orders out for delivery");
        return orderRepository.findOrdersOutForDelivery();
    }

    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching orders from {} to {}", startDate, endDate);
        return orderRepository.findOrdersByDateRange(startDate, endDate);
    }

    public List<Order> getRecentOrders(Pageable pageable) {
        log.info("Fetching recent orders");
        return orderRepository.findRecentOrders(pageable);
    }

    public Long getOrderCountByStatus(OrderStatus status) {
        return orderRepository.countOrdersByStatus(status);
    }

    public Long getRestaurantOrderCountByStatus(Long restaurantId, OrderStatus status) {
        return orderRepository.countOrdersByRestaurantAndStatus(restaurantId, status);
    }

    public Long getUserOrderCountByStatus(Long userId, OrderStatus status) {
        return orderRepository.countOrdersByUserAndStatus(userId, status);
    }

    public Double calculateRevenueByStatus(OrderStatus status) {
        return orderRepository.calculateRevenueByStatus(status);
    }

    public Double calculateRestaurantRevenue(Long restaurantId, OrderStatus status) {
        return orderRepository.calculateRevenueByRestaurantAndStatus(restaurantId, status);
    }

    private void calculateOrderTotals(Order order) {
        BigDecimal subtotal = order.getSubtotal() != null ? order.getSubtotal() : BigDecimal.ZERO;
        BigDecimal deliveryFee = order.getDeliveryFee() != null ? order.getDeliveryFee() : BigDecimal.ZERO;
        BigDecimal taxRate = new BigDecimal("0.08");
        BigDecimal taxAmount = subtotal.multiply(taxRate);
        BigDecimal tipAmount = order.getTipAmount() != null ? order.getTipAmount() : BigDecimal.ZERO;

        order.setTaxAmount(taxAmount);
        order.setTotalAmount(subtotal.add(deliveryFee).add(taxAmount).add(tipAmount));
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        switch (currentStatus) {
            case PENDING:
                if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.CANCELLED) {
                    throw new RuntimeException("Invalid status transition from PENDING to " + newStatus);
                }
                break;
            case CONFIRMED:
                if (newStatus != OrderStatus.PREPARING && newStatus != OrderStatus.CANCELLED) {
                    throw new RuntimeException("Invalid status transition from CONFIRMED to " + newStatus);
                }
                break;
            case PREPARING:
                if (newStatus != OrderStatus.READY_FOR_PICKUP && newStatus != OrderStatus.CANCELLED) {
                    throw new RuntimeException("Invalid status transition from PREPARING to " + newStatus);
                }
                break;
            case READY_FOR_PICKUP:
                if (newStatus != OrderStatus.OUT_FOR_DELIVERY && newStatus != OrderStatus.CANCELLED) {
                    throw new RuntimeException("Invalid status transition from READY_FOR_PICKUP to " + newStatus);
                }
                break;
            case OUT_FOR_DELIVERY:
                if (newStatus != OrderStatus.DELIVERED) {
                    throw new RuntimeException("Invalid status transition from OUT_FOR_DELIVERY to " + newStatus);
                }
                break;
            case DELIVERED:
            case CANCELLED:
            case REFUNDED:
                throw new RuntimeException("Cannot change status from " + currentStatus);
        }
    }
}