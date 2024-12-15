package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer periodDays;
    private boolean active;
    private LocalDateTime createdAt;
    
    private Long productId;
    private String productName;
    
    private Long organisationId;
    private String organisationName;
} 