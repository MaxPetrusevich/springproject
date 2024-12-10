package com.spring.springproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidHistoryDto {
    private Long id;
    private LocalDate date;
    private String serviceName;
    private String establishmentName;
    private String status;
    private BigDecimal amount;
    private List<StatusHistoryItem> statusTimeline;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusHistoryItem {
        private String status;
        private LocalDateTime date;
        private String comment;
        private boolean isActive;
    }
} 