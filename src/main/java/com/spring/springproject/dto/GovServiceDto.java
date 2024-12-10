package com.spring.springproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GovServiceDto {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Long establishmentId;
    private String establishmentName;
} 