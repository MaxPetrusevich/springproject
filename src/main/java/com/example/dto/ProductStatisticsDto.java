package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatisticsDto {
    private BigDecimal monthlyRevenue;
    private BigDecimal totalRevenue;
    private long activeSubscriptions;
    private long totalSubscriptions;
    private double renewalRate;
}