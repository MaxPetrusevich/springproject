package com.example.dto;

import com.example.enums.SubscriptionStatus;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSubscriptionDto {
    private Long id;
    private String productName;
    private String planName;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal price;
    private boolean canCancel;
    private String statusBadgeClass;
    private String statusText;
} 