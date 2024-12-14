package com.example.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganiserProductDto {
    private Long id;
    private String name;
    private String description;
    private boolean active;
    private Long organisationId;
    private String organisationName;
    private LocalDateTime createdAt;
    
    private long subscriptionPlansCount;
    private long activeSubscriptionsCount;
    private double monthlyIncome;
    private double totalIncome;
} 