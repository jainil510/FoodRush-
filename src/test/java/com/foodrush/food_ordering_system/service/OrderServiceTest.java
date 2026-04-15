package com.foodrush.food_ordering_system.service;

import com.foodrush.food_ordering_system.TestBase;
import com.foodrush.food_ordering_system.entity.Order;
import com.foodrush.food_ordering_system.entity.Restaurant;
import com.foodrush.food_ordering_system.entity.User;
import com.foodrush.food_ordering_system.enums.*;
import com.foodrush.food_ordering_system.exception.ResourceNotFoundException;
import com.foodrush.food_ordering_system.repository.OrderRepository;
import com.foodrush.food_ordering_system.repository.RestaurantRepository;
import com.foodrush.food_ordering_system.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest extends TestBase {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private User testUser;
    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testRestaurant = new Restaurant();
        testRestaurant.setId(1L);
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setStatus(RestaurantStatus.APPROVED);
        testRestaurant.setIsOpen(true);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setRestaurant(testRestaurant);
        testOrder.setSubtotal(new BigDecimal("100.00"));
        testOrder.setDeliveryFee(new BigDecimal("5.00"));
        testOrder.setTaxAmount(new BigDecimal("8.00"));
        testOrder.setTotalAmount(new BigDecimal("123.00"));
        testOrder.setTipAmount(new BigDecimal("10.00"));
        testOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        testOrder.setOrderStatus(OrderStatus.PENDING);
        testOrder.setPaymentStatus(PaymentStatus.PENDING);
    }

    @Test
    void testCreateOrder_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.createOrder(testOrder);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(testRestaurant, result.getRestaurant());
        assertEquals(OrderStatus.PENDING, result.getOrderStatus());
        assertEquals(PaymentStatus.PENDING, result.getPaymentStatus());

        verify(userRepository).findById(1L);
        verify(restaurantRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testCreateOrder_RestaurantClosed() {
        // Arrange
        testRestaurant.setIsOpen(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                orderService.createOrder(testOrder)
        );

        assertEquals("Restaurant is currently closed", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(restaurantRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCreateOrder_RestaurantNotApproved() {
        // Arrange
        testRestaurant.setStatus(RestaurantStatus.PENDING);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                orderService.createOrder(testOrder)
        );

        assertEquals("Restaurant is not approved for orders", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(restaurantRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetOrderById_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        Order result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testOrder, result);
        verify(orderRepository).findById(1L);
    }

    @Test
    void testGetOrderById_NotFound() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                orderService.getOrderById(999L)
        );

        assertEquals("Order not found with id: 999", exception.getMessage());
        verify(orderRepository).findById(999L);
    }

    @Test
    void testGetOrderByIdAndUser_Success() {
        // Arrange
        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testOrder));

        // Act
        Order result = orderService.getOrderByIdAndUser(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(testOrder, result);
        verify(orderRepository).findByIdAndUserId(1L, 1L);
    }

    @Test
    void testGetOrdersByUser() {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findOrdersByUserOrderByDate(1L)).thenReturn(orders);

        // Act
        List<Order> result = orderService.getOrdersByUser(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findOrdersByUserOrderByDate(1L);
    }

    @Test
    void testGetOrdersByRestaurant() {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findOrdersByRestaurantOrderByDate(1L)).thenReturn(orders);

        // Act
        List<Order> result = orderService.getOrdersByRestaurant(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findOrdersByRestaurantOrderByDate(1L);
    }

    @Test
    void testUpdateOrderStatus_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.CONFIRMED, result.getOrderStatus());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testConfirmOrder() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.confirmOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.CONFIRMED, result.getOrderStatus());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testStartPreparingOrder() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.startPreparingOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.PREPARING, result.getOrderStatus());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testMarkOrderReadyForPickup() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.markOrderReadyForPickup(1L);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.READY_FOR_PICKUP, result.getOrderStatus());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testCancelOrder_Success() {
        // Arrange
        testOrder.setOrderStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.cancelOrder(1L, "Customer requested cancellation");

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.getOrderStatus());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testCancelOrder_DeliveredOrder() {
        // Arrange
        testOrder.setOrderStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                orderService.cancelOrder(1L, "Cannot cancel delivered order")
        );

        assertEquals("Cannot cancel delivered order", exception.getMessage());
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testUpdatePaymentStatus() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.updatePaymentStatus(1L, PaymentStatus.PAID);

        // Assert
        assertNotNull(result);
        assertEquals(PaymentStatus.PAID, result.getPaymentStatus());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testAssignDriver() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.assignDriver(1L, "John Driver", "9876543210");

        // Assert
        assertNotNull(result);
        assertEquals("John Driver", result.getDriverName());
        assertEquals("9876543210", result.getDriverPhone());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testMarkOrderOutForDelivery() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.markOrderOutForDelivery(1L);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.OUT_FOR_DELIVERY, result.getOrderStatus());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testMarkOrderDelivered() {
        // Arrange
        testOrder.setOrderStatus(OrderStatus.OUT_FOR_DELIVERY);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.markOrderDelivered(1L, 25);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.DELIVERED, result.getOrderStatus());
        assertEquals(25, result.getActualDeliveryTime());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testGetOrdersByStatus() {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByOrderStatus(OrderStatus.PENDING)).thenReturn(orders);

        // Act
        List<Order> result = orderService.getOrdersByStatus(OrderStatus.PENDING);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findByOrderStatus(OrderStatus.PENDING);
    }

    @Test
    void testGetActiveOrders() {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findActiveOrders(any())).thenReturn(orders);

        // Act
        List<Order> result = orderService.getActiveOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findActiveOrders(any());
    }

    @Test
    void testGetOrderCountByStatus() {
        // Arrange
        when(orderRepository.countOrdersByStatus(OrderStatus.PENDING)).thenReturn(5L);

        // Act
        Long result = orderService.getOrderCountByStatus(OrderStatus.PENDING);

        // Assert
        assertEquals(5L, result);
        verify(orderRepository).countOrdersByStatus(OrderStatus.PENDING);
    }

    @Test
    void testCalculateRevenueByStatus() {
        // Arrange
        when(orderRepository.calculateRevenueByStatus(OrderStatus.DELIVERED)).thenReturn(1000.50);

        // Act
        Double result = orderService.calculateRevenueByStatus(OrderStatus.DELIVERED);

        // Assert
        assertEquals(1000.50, result);
        verify(orderRepository).calculateRevenueByStatus(OrderStatus.DELIVERED);
    }
}
