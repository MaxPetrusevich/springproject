package com.example.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrganisationStatisticsDto {
    private long totalProducts;
    private long activeProducts;
    private long totalSubscribers;
    private long activeSubscribers;
    private BigDecimal monthlyRevenue;
    private BigDecimal totalRevenue;
    private int newSubscribersThisMonth;
    private double churnRate;
    private double conversionRate;
} 