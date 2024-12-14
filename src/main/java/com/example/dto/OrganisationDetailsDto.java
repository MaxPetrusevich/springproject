package com.example.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganisationDetailsDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String ownerName;
    private boolean active;
    private LocalDateTime createdAt;
    
    private long productsCount;
    private long activeProductsCount;
    private long subscriptionPlansCount;
    private long activeSubscriptionPlansCount;
    private long activeSubscriptionsCount;
    private long totalSubscriptionsCount;
    private double monthlyIncome;
    private double totalIncome;
    
    private List<OrganiserProductDto> products;
} 