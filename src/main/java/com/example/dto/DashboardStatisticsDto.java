package com.example.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsDto {
    private double totalRevenue;
    private double monthlyRevenue;
    private long activeSubscriptionsCount;
    private double renewalRate;
} 