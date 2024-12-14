package com.example.dto;

import com.example.enums.SubscriptionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class SubscriptionStatisticsDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private SubscriptionStatus status;
    private boolean active;
    
    private BigDecimal totalPaid;
    private double paymentSuccessRate;
    
    private Map<String, BigDecimal> paymentHistory;
} 