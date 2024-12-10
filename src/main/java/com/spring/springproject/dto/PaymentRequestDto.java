package com.spring.springproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PaymentRequestDto {
    
    @NotNull(message = "ID заявки обязателен")
    private Long bidId;
    
    @NotNull(message = "Статус платежа обязателен")
    private Long statusId;
    
    @NotNull(message = "Сумма платежа обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Сумма платежа должна быть больше 0")
    private BigDecimal amount;
    
    @NotNull(message = "Дата платежа обязательна")
    private LocalDateTime paymentDate;
    
    private String description;
    
    private String transactionId;
    
    // Дополнительные поля для платежной системы
    private String paymentSystem;
    
    private String paymentMethod;
    
    // Поля для отслеживания статуса
    private LocalDateTime processedDate;
    
    private String errorMessage;
    
    // Поля для возврата
    private Boolean isRefund;
    
    private Long originalPaymentId;
    
    private String refundReason;
}