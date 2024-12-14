package com.example.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrganiserStatisticsDto {
    private long activeSubscriptions;
    private long totalSubscriptions;
    private BigDecimal monthlyRevenue;
    private BigDecimal totalRevenue;
} 