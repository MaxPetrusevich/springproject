package com.spring.springproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class BidStatusRequestDto {
    @NotBlank
    private String status;
    
    private String description;
}