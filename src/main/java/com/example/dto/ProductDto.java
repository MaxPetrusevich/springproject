package com.example.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private String category;
    private boolean active;
    private String organisationName;
    private Long organisationId;
    private List<SubscriptionPlanDto> subscriptionPlans;
    
    private long subscriptionPlansCount;
    private long activeSubscriptionsCount;
} 