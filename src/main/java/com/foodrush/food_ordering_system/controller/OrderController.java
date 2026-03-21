package com.foodrush.food_ordering_system.controller;

import com.foodrush.food_ordering_system.dto.request.OrderRequest;
import com.foodrush.food_ordering_system.dto.request.OrderStatusUpdateRequest;
import com.foodrush.food_ordering_system.dto.response.OrderResponse;
import com.foodrush.food_ordering_system.entity.Order;
import com.foodrush.food_ordering_system.enums.OrderStatus;
import com.foodrush.food_ordering_system.enums.PaymentStatus;
import com.foodrush.food_ordering_system.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    private Order convertToEntity(OrderRequest orderRequest, Long userId) {
        Order order = new Order();

        com.foodrush.food_ordering_system.entity.User user = new com.foodrush.food_ordering_system.entity.User();
        user.setId(userId);
        order.setUser(user);

        com.foodrush.food_ordering_system.entity.Restaurant restaurant = new com.foodrush.food_ordering_system.entity.Restaurant();
        restaurant.setId(orderRequest.getRestaurantId());
        order.setRestaurant(restaurant);

        order.setDeliveryAddress(orderRequest.getDeliveryAddress());
        order.setDeliveryCity(orderRequest.getDeliveryCity());
        order.setDeliveryState(orderRequest.getDeliveryState());
        order.setDeliveryZipCode(orderRequest.getDeliveryZipCode());
        order.setDeliveryPhone(orderRequest.getDeliveryPhone());

        order.setSubtotal(orderRequest.getSubtotal());
        order.setTipAmount(orderRequest.getTipAmount());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setSpecialInstructions(orderRequest.getSpecialInstructions());
        order.setEstimatedDeliveryTime(orderRequest.getEstimatedDeliveryTime());

        return order;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        try {
            log.info("Creating new order for user: {}", userDetails.getUsername());
            Long userId = 1L;

            Order order = convertToEntity(orderRequest, userId);
            Order createdOrder = orderService.createOrder(order);

            OrderResponse response = OrderResponse.fromOrder(createdOrder);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        try {
            log.info("Fetching order with ID: {} for user: {}", id, userDetails.getUsername());
            Order order = orderService.getOrderById(id);
            OrderResponse response = OrderResponse.fromOrder(order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            log.info("Fetching orders for user: {}", userDetails.getUsername());
            Long userId = 1L;
            List<Order> orders = orderService.getOrdersByUser(userId);
            List<OrderResponse> responses = orders.stream()
                    .map(OrderResponse::minimalFromOrder)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching user orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/my-orders/paginated")
    public ResponseEntity<Page<OrderResponse>> getMyOrdersPaginated(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        try {
            log.info("Fetching paginated orders for user: {}", userDetails.getUsername());
            Long userId = 1L;
            Page<Order> orders = orderService.getOrdersByUserWithPagination(userId, pageable);
            Page<OrderResponse> responses = orders.map(OrderResponse::minimalFromOrder);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching paginated user orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/my-orders/status/{status}")
    public ResponseEntity<List<OrderResponse>> getMyOrdersByStatus(
            @PathVariable OrderStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            log.info("Fetching orders with status {} for user: {}", status, userDetails.getUsername());
            Long userId = 1L;
            List<Order> orders = orderService.getOrdersByUserAndStatus(userId, status);
            List<OrderResponse> responses = orders.stream()
                    .map(OrderResponse::minimalFromOrder)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching user orders by status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/my-orders/search")
    public ResponseEntity<List<OrderResponse>> searchMyOrders(
            @RequestParam String searchTerm,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            log.info("Searching orders for user: {} with term: {}", userDetails.getUsername(), searchTerm);
            Long userId = 1L;
            List<Order> orders = orderService.searchUserOrders(userId, searchTerm);
            List<OrderResponse> responses = orders.stream()
                    .map(OrderResponse::minimalFromOrder)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error searching user orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateRequest statusRequest) {
        try {
            log.info("Updating order {} status to: {}", id, statusRequest.getOrderStatus());
            Order updatedOrder = orderService.updateOrderStatus(id, statusRequest.getOrderStatus());
            OrderResponse response = OrderResponse.fromOrder(updatedOrder);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating order status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable Long id) {
        try {
            log.info("Confirming order: {}", id);
            Order confirmedOrder = orderService.confirmOrder(id);
            OrderResponse response = OrderResponse.fromOrder(confirmedOrder);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error confirming order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/start-preparing")
    public ResponseEntity<OrderResponse> startPreparingOrder(@PathVariable Long id) {
        try {
            log.info("Starting preparation for order: {}", id);
            Order order = orderService.startPreparingOrder(id);
            OrderResponse response = OrderResponse.fromOrder(order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error starting order preparation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/ready-for-pickup")
    public ResponseEntity<OrderResponse> markOrderReadyForPickup(@PathVariable Long id) {
        try {
            log.info("Marking order {} as ready for pickup", id);
            Order order = orderService.markOrderReadyForPickup(id);
            OrderResponse response = OrderResponse.fromOrder(order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error marking order ready for pickup: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long id,
            @RequestBody(required = false) OrderStatusUpdateRequest cancelRequest) {
        try {
            String reason = cancelRequest != null ? cancelRequest.getReason() : null;
            log.info("Cancelling order: {} with reason: {}", id, reason);
            Order cancelledOrder = orderService.cancelOrder(id, reason);
            OrderResponse response = OrderResponse.fromOrder(cancelledOrder);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error cancelling order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<OrderResponse> updatePaymentStatus(
            @PathVariable Long id,
            @RequestBody PaymentStatus paymentStatus) {
        try {
            log.info("Updating payment status for order {} to: {}", id, paymentStatus);
            Order updatedOrder = orderService.updatePaymentStatus(id, paymentStatus);
            OrderResponse response = OrderResponse.fromOrder(updatedOrder);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating payment status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/assign-driver")
    public ResponseEntity<OrderResponse> assignDriver(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateRequest driverRequest) {
        try {
            log.info("Assigning driver {} to order: {}", driverRequest.getDriverName(), id);
            Order order = orderService.assignDriver(id, driverRequest.getDriverName(), driverRequest.getDriverPhone());
            OrderResponse response = OrderResponse.fromOrder(order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error assigning driver: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/out-for-delivery")
    public ResponseEntity<OrderResponse> markOrderOutForDelivery(@PathVariable Long id) {
        try {
            log.info("Marking order {} as out for delivery", id);
            Order order = orderService.markOrderOutForDelivery(id);
            OrderResponse response = OrderResponse.fromOrder(order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error marking order out for delivery: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/delivered")
    public ResponseEntity<OrderResponse> markOrderDelivered(
            @PathVariable Long id,
            @RequestBody(required = false) OrderStatusUpdateRequest deliveryRequest) {
        try {
            Integer actualDeliveryTime = deliveryRequest != null ? deliveryRequest.getActualDeliveryTime() : null;
            log.info("Marking order {} as delivered", id);
            Order order = orderService.markOrderDelivered(id, actualDeliveryTime);
            OrderResponse response = OrderResponse.fromOrder(order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error marking order delivered: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}