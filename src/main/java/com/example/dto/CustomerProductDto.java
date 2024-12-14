package com.example.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CustomerProductDto {
    private Long id;
    private String name;
    private String description;
    private String category;
    private boolean active;
    private String organisationName;
    private Long organisationId;
    private List<SubscriptionPlanDto> plans;

    private long availablePlansCount;
}