package com.example.dto;

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
public class OrganiserSubscriptionPlanDto {
    private Long id;
    private String name;
    private String description;
    private boolean active;
    private BigDecimal price;
    private int periodDays;
    private LocalDateTime createdAt;
    
    private Long productId;
    private String productName;
    private String organisationName;
    
    private long activeSubscriptionsCount;
    private long totalSubscriptionsCount;
    private double renewalRate;
    private double monthlyIncome;
    private double totalIncome;
    private int newSubscriptionsThisMonth;

    public String getStatusBadgeClass() {
        return active ? "bg-success" : "bg-danger";
    }
    
    public String getStatusText() {
        return active ? "Активен" : "Неактивен";
    }
} 