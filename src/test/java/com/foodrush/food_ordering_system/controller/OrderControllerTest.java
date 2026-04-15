package com.foodrush.food_ordering_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodrush.food_ordering_system.TestBase;
import com.foodrush.food_ordering_system.dto.request.OrderRequest;
import com.foodrush.food_ordering_system.dto.request.OrderStatusUpdateRequest;
import com.foodrush.food_ordering_system.dto.response.OrderResponse;
import com.foodrush.food_ordering_system.entity.Order;
import com.foodrush.food_ordering_system.enums.OrderStatus;
import com.foodrush.food_ordering_system.enums.PaymentMethod;
import com.foodrush.food_ordering_system.enums.PaymentStatus;
import com.foodrush.food_ordering_system.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest extends TestBase {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private MockMvc mockMvc;
    private OrderRequest testOrderRequest;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        testOrderRequest = new OrderRequest();
        testOrderRequest.setRestaurantId(1L);
        testOrderRequest.setDeliveryAddress("123 Test St");
        testOrderRequest.setDeliveryCity("Test City");
        testOrderRequest.setDeliveryState("Test State");
        testOrderRequest.setDeliveryZipCode("12345");
        testOrderRequest.setDeliveryPhone("1234567890");
        testOrderRequest.setSubtotal(new BigDecimal("100.00"));
        testOrderRequest.setTipAmount(new BigDecimal("10.00"));
        testOrderRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        testOrderRequest.setSpecialInstructions("Extra napkins");

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderStatus(OrderStatus.PENDING);
        testOrder.setPaymentStatus(PaymentStatus.PENDING);
        testOrder.setTotalAmount(new BigDecimal("123.00"));
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        // Arrange
        when(orderService.createOrder(any(Order.class))).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderStatus").value("PENDING"))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"));

        verify(orderService).createOrder(any(Order.class));
    }

    @Test
    void testCreateOrder_MissingRequiredFields() throws Exception {
        // Arrange
        OrderRequest invalidRequest = new OrderRequest();
        // Missing all required fields

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any(Order.class));
    }

    @Test
    void testCreateOrder_InvalidPaymentMethod() throws Exception {
        // Arrange
        testOrderRequest.setPaymentMethod(null); // Missing payment method

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrderRequest)))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any(Order.class));
    }

    @Test
    void testGetOrderById_Success() throws Exception {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderStatus").value("PENDING"));

        verify(orderService).getOrderById(1L);
    }

    @Test
    void testGetOrderById_NotFound() throws Exception {
        // Arrange
        when(orderService.getOrderById(999L))
                .thenThrow(new RuntimeException("Order not found"));

        // Act & Assert
        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound());

        verify(orderService).getOrderById(999L);
    }

    @Test
    void testGetMyOrders_Success() throws Exception {
        // Arrange
        when(orderService.getOrdersByUser(anyLong()))
                .thenReturn(Arrays.asList(testOrder));

        // Act & Assert
        mockMvc.perform(get("/api/orders/my-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].orderStatus").value("PENDING"));

        verify(orderService).getOrdersByUser(anyLong());
    }

    @Test
    void testGetMyOrdersByStatus_Success() throws Exception {
        // Arrange
        when(orderService.getOrdersByUserAndStatus(anyLong(), any(OrderStatus.class)))
                .thenReturn(Arrays.asList(testOrder));

        // Act & Assert
        mockMvc.perform(get("/api/orders/my-orders/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderStatus").value("PENDING"));

        verify(orderService).getOrdersByUserAndStatus(anyLong(), eq(OrderStatus.PENDING));
    }

    @Test
    void testUpdateOrderStatus_Success() throws Exception {
        // Arrange
        OrderStatusUpdateRequest statusRequest = new OrderStatusUpdateRequest();
        statusRequest.setOrderStatus(OrderStatus.CONFIRMED);

        testOrder.setOrderStatus(OrderStatus.CONFIRMED);
        when(orderService.updateOrderStatus(anyLong(), any(OrderStatus.class)))
                .thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(put("/api/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CONFIRMED"));

        verify(orderService).updateOrderStatus(1L, OrderStatus.CONFIRMED);
    }

    @Test
    void testConfirmOrder_Success() throws Exception {
        // Arrange
        testOrder.setOrderStatus(OrderStatus.CONFIRMED);
        when(orderService.confirmOrder(1L)).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(put("/api/orders/1/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CONFIRMED"));

        verify(orderService).confirmOrder(1L);
    }

    @Test
    void testStartPreparingOrder_Success() throws Exception {
        // Arrange
        testOrder.setOrderStatus(OrderStatus.PREPARING);
        when(orderService.startPreparingOrder(1L)).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(put("/api/orders/1/start-preparing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("PREPARING"));

        verify(orderService).startPreparingOrder(1L);
    }

    @Test
    void testMarkOrderReadyForPickup_Success() throws Exception {
        // Arrange
        testOrder.setOrderStatus(OrderStatus.READY_FOR_PICKUP);
        when(orderService.markOrderReadyForPickup(1L)).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(put("/api/orders/1/ready-for-pickup"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("READY_FOR_PICKUP"));

        verify(orderService).markOrderReadyForPickup(1L);
    }

    @Test
    void testCancelOrder_Success() throws Exception {
        // Arrange
        OrderStatusUpdateRequest cancelRequest = new OrderStatusUpdateRequest();
        cancelRequest.setReason("Customer requested cancellation");

        testOrder.setOrderStatus(OrderStatus.CANCELLED);
        when(orderService.cancelOrder(anyLong(), anyString())).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(put("/api/orders/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CANCELLED"));

        verify(orderService).cancelOrder(1L, "Customer requested cancellation");
    }

    @Test
    void testUpdatePaymentStatus_Success() throws Exception {
        // Arrange
        testOrder.setPaymentStatus(PaymentStatus.PAID);
        when(orderService.updatePaymentStatus(anyLong(), any(PaymentStatus.class)))
                .thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(put("/api/orders/1/payment-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(PaymentStatus.PAID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));

        verify(orderService).updatePaymentStatus(1L, PaymentStatus.PAID);
    }

    @Test
    void testAssignDriver_Success() throws Exception {
        // Arrange
        OrderStatusUpdateRequest driverRequest = new OrderStatusUpdateRequest();
        driverRequest.setDriverName("John Driver");
        driverRequest.setDriverPhone("9876543210");

        testOrder.setDriverName("John Driver");
        testOrder.setDriverPhone("9876543210");
        when(orderService.assignDriver(anyLong(), anyString(), anyString())).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(put("/api/orders/1/assign-driver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverName").value("John Driver"))
                .andExpect(jsonPath("$.driverPhone").value("9876543210"));

        verify(orderService).assignDriver(1L, "John Driver", "9876543210");
    }

    @Test
    void testMarkOrderOutForDelivery_Success() throws Exception {
        // Arrange
        testOrder.setOrderStatus(OrderStatus.OUT_FOR_DELIVERY);
        when(orderService.markOrderOutForDelivery(1L)).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(put("/api/orders/1/out-for-delivery"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("OUT_FOR_DELIVERY"));

        verify(orderService).markOrderOutForDelivery(1L);
    }

    @Test
    void testMarkOrderDelivered_Success() throws Exception {
        // Arrange
        OrderStatusUpdateRequest deliveryRequest = new OrderStatusUpdateRequest();
        deliveryRequest.setActualDeliveryTime(25);

        testOrder.setOrderStatus(OrderStatus.DELIVERED);
        testOrder.setActualDeliveryTime(25);
        when(orderService.markOrderDelivered(anyLong(), anyInt())).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(put("/api/orders/1/delivered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("DELIVERED"))
                .andExpect(jsonPath("$.actualDeliveryTime").value(25));

        verify(orderService).markOrderDelivered(1L, 25);
    }

    @Test
    void testCreateOrder_InvalidSubtotal() throws Exception {
        // Arrange
        testOrderRequest.setSubtotal(new BigDecimal("-100.00")); // Negative subtotal

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrderRequest)))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any(Order.class));
    }

    @Test
    void testCreateOrder_InvalidDeliveryAddress() throws Exception {
        // Arrange
        testOrderRequest.setDeliveryAddress(""); // Empty address

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrderRequest)))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any(Order.class));
    }

    @Test
    void testCreateOrder_InvalidPhone() throws Exception {
        // Arrange
        testOrderRequest.setDeliveryPhone("123"); // Too short

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrderRequest)))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any(Order.class));
    }
}
