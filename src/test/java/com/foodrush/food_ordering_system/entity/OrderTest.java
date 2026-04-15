package com.foodrush.food_ordering_system.entity;

import com.foodrush.food_ordering_system.TestBase;
import com.foodrush.food_ordering_system.enums.OrderStatus;
import com.foodrush.food_ordering_system.enums.PaymentMethod;
import com.foodrush.food_ordering_system.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest extends TestBase {

    private Order order;
    private User testUser;
    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        order = new Order();
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        
        testRestaurant = new Restaurant();
        testRestaurant.setId(1L);
        testRestaurant.setName("Test Restaurant");
    }

    @Test
    void testOrderCreation() {
        order.setUser(testUser);
        order.setRestaurant(testRestaurant);
        order.setDeliveryAddress("123 Test St");
        order.setDeliveryCity("Test City");
        order.setDeliveryState("Test State");
        order.setDeliveryZipCode("12345");
        order.setDeliveryPhone("1234567890");
        order.setSubtotal(new BigDecimal("100.00"));
        order.setDeliveryFee(new BigDecimal("5.00"));
        order.setTipAmount(new BigDecimal("10.00"));
        order.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        assertNotNull(order);
        assertEquals("123 Test St", order.getDeliveryAddress());
        assertEquals("Test City", order.getDeliveryCity());
        assertEquals(new BigDecimal("100.00"), order.getSubtotal());
        assertEquals(PaymentMethod.CREDIT_CARD, order.getPaymentMethod());
    }

    @Test
    void testOrderPrePersist() {
        // Set required fields
        order.setSubtotal(new BigDecimal("100.00"));
        order.setDeliveryFee(new BigDecimal("5.00"));
        order.setTaxAmount(new BigDecimal("8.00"));
        order.setTipAmount(new BigDecimal("10.00"));

        // Test @PrePersist method
        order.onCreate();

        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
        assertEquals(BigDecimal.ZERO, order.getTipAmount());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    void testOrderPreUpdate() {
        // Set initial values
        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setDeliveredAt(null);

        // Test @PreUpdate method
        order.setOrderStatus(OrderStatus.DELIVERED);
        order.onUpdate();

        assertNotNull(order.getUpdatedAt());
        assertNotNull(order.getDeliveredAt());
        assertEquals(OrderStatus.DELIVERED, order.getOrderStatus());
    }

    @Test
    void testOrderTotalCalculation() {
        order.setSubtotal(new BigDecimal("100.00"));
        order.setDeliveryFee(new BigDecimal("5.00"));
        order.setTaxAmount(new BigDecimal("8.00"));
        order.setTipAmount(new BigDecimal("10.00"));

        // Call calculateTotalAmount method
        order.calculateTotalAmount();

        assertEquals(new BigDecimal("123.00"), order.getTotalAmount());
    }

    @Test
    void testOrderTotalCalculationWithNullValues() {
        order.setSubtotal(null);
        order.setDeliveryFee(null);
        order.setTaxAmount(null);
        order.setTipAmount(null);

        order.calculateTotalAmount();

        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
    }

    @Test
    void testOrderStatusTransitions() {
        order.setOrderStatus(OrderStatus.PENDING);
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());

        order.setOrderStatus(OrderStatus.CONFIRMED);
        assertEquals(OrderStatus.CONFIRMED, order.getOrderStatus());

        order.setOrderStatus(OrderStatus.PREPARING);
        assertEquals(OrderStatus.PREPARING, order.getOrderStatus());

        order.setOrderStatus(OrderStatus.READY_FOR_PICKUP);
        assertEquals(OrderStatus.READY_FOR_PICKUP, order.getOrderStatus());

        order.setOrderStatus(OrderStatus.OUT_FOR_DELIVERY);
        assertEquals(OrderStatus.OUT_FOR_DELIVERY, order.getOrderStatus());

        order.setOrderStatus(OrderStatus.DELIVERED);
        assertEquals(OrderStatus.DELIVERED, order.getOrderStatus());

        order.setOrderStatus(OrderStatus.CANCELLED);
        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
    }

    @Test
    void testPaymentStatusTransitions() {
        order.setPaymentStatus(PaymentStatus.PENDING);
        assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());

        order.setPaymentStatus(PaymentStatus.PAID);
        assertEquals(PaymentStatus.PAID, order.getPaymentStatus());

        order.setPaymentStatus(PaymentStatus.FAILED);
        assertEquals(PaymentStatus.FAILED, order.getPaymentStatus());

        order.setPaymentStatus(PaymentStatus.REFUNDED);
        assertEquals(PaymentStatus.REFUNDED, order.getPaymentStatus());
    }

    @Test
    void testPaymentMethods() {
        order.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        assertEquals(PaymentMethod.CREDIT_CARD, order.getPaymentMethod());

        order.setPaymentMethod(PaymentMethod.DEBIT_CARD);
        assertEquals(PaymentMethod.DEBIT_CARD, order.getPaymentMethod());

        order.setPaymentMethod(PaymentMethod.DIGITAL_WALLET);
        assertEquals(PaymentMethod.DIGITAL_WALLET, order.getPaymentMethod());

        order.setPaymentMethod(PaymentMethod.CASH);
        assertEquals(PaymentMethod.CASH, order.getPaymentMethod());
    }

    @Test
    void testDeliveryTimeTracking() {
        order.setEstimatedDeliveryTime(30);
        assertEquals(30, order.getEstimatedDeliveryTime());

        order.setActualDeliveryTime(25);
        assertEquals(25, order.getActualDeliveryTime());

        // Test delivery was faster than estimated
        assertTrue(order.getActualDeliveryTime() < order.getEstimatedDeliveryTime());
    }

    @Test
    void testSpecialInstructions() {
        String instructions = "Extra napkins, no onions";
        order.setSpecialInstructions(instructions);
        assertEquals(instructions, order.getSpecialInstructions());
    }

    @Test
    void testDriverAssignment() {
        String driverName = "John Driver";
        String driverPhone = "9876543210";

        order.setDriverName(driverName);
        order.setDriverPhone(driverPhone);

        assertEquals(driverName, order.getDriverName());
        assertEquals(driverPhone, order.getDriverPhone());
    }
}
