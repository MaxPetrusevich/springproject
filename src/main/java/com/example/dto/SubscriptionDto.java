package com.example.dto;

import com.example.enums.SubscriptionStatus;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDto {
    private Long id;
    
    private Long userId;
    private String userEmail;
    private String userName;
    
    private Long planId;
    private String planName;
    private BigDecimal planPrice;
    private Integer planPeriodDays;
    
    private Long productId;
    private String productName;
    private Long organisationId;
    private String organisationName;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime cancelledAt;
    private LocalDateTime renewedAt;
    private SubscriptionStatus status;
    private boolean active;
    
    private BigDecimal price;
    private LocalDateTime lastPaymentDate;
    private LocalDateTime nextPaymentDate;
    private boolean autoRenewal;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 