package com.example.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDetailsDto {
    private Long id;
    private String name;
    private String description;
    private boolean active;
    private Long organisationId;
    private String organisationName;
    private LocalDateTime createdAt;
    private List<SubscriptionPlanDto> plans;
    private int totalPlans;
    private int activePlans;
} 