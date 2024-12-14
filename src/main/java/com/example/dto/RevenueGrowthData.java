package com.example.dto;

import lombok.Value;
import java.math.BigDecimal;
import java.time.LocalDate;

@Value
public class RevenueGrowthData {
    LocalDate date;
    BigDecimal amount;
} 