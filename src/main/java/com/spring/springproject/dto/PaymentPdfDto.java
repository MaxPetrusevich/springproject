package com.spring.springproject.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPdfDto {

    private Long paymentId;
    private Long bidId;
    private BigDecimal amount;
    private LocalDate paymentDate;
    
    // Информация об услуге
    private String serviceName;
    private String serviceDescription;
    
    // Информация о плательщике
    private String citizenName;
    private String citizenIdentifyNumber;
    private String citizenPhone;
    private String citizenEmail;
    
    // Информация об учреждении
    private String establishmentName;
    private String establishmentAddress;
    private String establishmentPhone;
    
    // Информация о статусе
    private String bidStatus;
}
