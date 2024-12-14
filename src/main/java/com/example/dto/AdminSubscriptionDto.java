package com.example.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSubscriptionDto {
    private Long id;
    
    private Long userId;
    private String userEmail;
    
    private Long productId;
    private String productName;
    
    private Long planId;
    private String planName;
    private BigDecimal price;
    
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    public String getStatusBadgeClass() {
        return switch (status) {
            case "ACTIVE" -> "badge-success";
            case "EXPIRED" -> "badge-warning";
            case "CANCELLED" -> "badge-danger";
            default -> "badge-secondary";
        };
    }
    
    public String getStatusText() {
        return switch (status) {
            case "ACTIVE" -> "Активна";
            case "EXPIRED" -> "Завершена";
            case "CANCELLED" -> "Отменена";
            default -> status;
        };
    }
}