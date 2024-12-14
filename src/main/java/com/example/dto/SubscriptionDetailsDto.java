package com.example.dto;

import com.example.enums.SubscriptionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SubscriptionDetailsDto {
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
    private SubscriptionStatus status;
    private LocalDateTime createdAt;
    private List<PaymentDto> payments;
} 