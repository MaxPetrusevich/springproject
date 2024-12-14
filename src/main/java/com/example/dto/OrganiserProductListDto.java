package com.example.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganiserProductListDto {
    private Long id;
    private String name;
    private String description;
    private String category;
    private boolean active;
    private Long organisationId;
    private String organisationName;
    private long subscriptionPlansCount;
    private long activeSubscriptionsCount;
    private double monthlyIncome;
    private double totalIncome;
    
    public String getStatusBadgeClass() {
        return active ? "bg-success" : "bg-danger";
    }
    
    public String getStatusText() {
        return active ? "Активен" : "Неактивен";
    }
} 