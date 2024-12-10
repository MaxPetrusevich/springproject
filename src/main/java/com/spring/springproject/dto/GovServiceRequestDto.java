package com.spring.springproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class GovServiceRequestDto {
    @NotBlank
    private String name;
    
    private String description;
    
    @NotNull
    @Min(0)
    private BigDecimal cost;
    
    @NotNull
    private Long categoryId;
    
    private Integer processingTime;
}