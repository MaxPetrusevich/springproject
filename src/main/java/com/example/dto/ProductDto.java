package com.example.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private String category;
    private boolean active;
    private Long organisationId;
    private String organisationName;
    private List<SubscriptionPlanDto> subscriptionPlans;

    private long subscriptionPlansCount;
    private long activeSubscriptionsCount;
} 