package com.foodrush.food_ordering_system.entity;

import com.foodrush.food_ordering_system.enums.OrderStatus;
import com.foodrush.food_ordering_system.enums.PaymentStatus;
import com.foodrush.food_ordering_system.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private String deliveryAddress;

    @Column(nullable = false)
    private String deliveryCity;

    @Column(nullable = false)
    private String deliveryState;

    @Column(nullable = false)
    private String deliveryZipCode;

    @Column(nullable = false)
    private String deliveryPhone;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryFee;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tipAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private Integer estimatedDeliveryTime;

    @Column
    private Integer actualDeliveryTime;

    @Column
    private LocalDateTime deliveredAt;

    @Column(length = 500)
    private String specialInstructions;

    @Column
    private String driverName;

    @Column
    private String driverPhone;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.orderStatus = OrderStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
        this.tipAmount = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        
        if (this.totalAmount == null && this.subtotal != null) {
            calculateTotalAmount();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        
        
        if (this.orderStatus == OrderStatus.DELIVERED && this.deliveredAt == null) {
            this.deliveredAt = LocalDateTime.now();
        }
        
        
        if (this.totalAmount == null && this.subtotal != null) {
            calculateTotalAmount();
        }
    }

    private void calculateTotalAmount() {
        BigDecimal subtotal = this.subtotal != null ? this.subtotal : BigDecimal.ZERO;
        BigDecimal deliveryFee = this.deliveryFee != null ? this.deliveryFee : BigDecimal.ZERO;
        BigDecimal taxAmount = this.taxAmount != null ? this.taxAmount : BigDecimal.ZERO;
        BigDecimal tipAmount = this.tipAmount != null ? this.tipAmount : BigDecimal.ZERO;
        
        this.totalAmount = subtotal.add(deliveryFee).add(taxAmount).add(tipAmount);
    }
}