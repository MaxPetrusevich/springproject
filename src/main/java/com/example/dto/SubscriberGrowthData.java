package com.example.dto;

import lombok.Value;
import java.time.LocalDate;

@Value
public class SubscriberGrowthData {
    LocalDate date;
    Integer count;
} 