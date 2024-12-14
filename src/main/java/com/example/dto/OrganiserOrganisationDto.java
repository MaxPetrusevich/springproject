package com.example.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
public class OrganiserOrganisationDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String ownerName;
    private boolean active;
    private LocalDateTime createdAt;
    
    private long productsCount;
    private long activeSubscriptionsCount;
    private long totalSubscriptionsCount;
    private double monthlyIncome;
    private double totalIncome;
} 