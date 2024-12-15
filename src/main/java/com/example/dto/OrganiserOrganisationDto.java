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
    private Boolean active;
    private Long productsCount;
    private Long activeSubscriptionsCount;
    private double monthlyIncome;

    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;

    private long totalSubscriptionsCount;
    private double totalIncome;
} 