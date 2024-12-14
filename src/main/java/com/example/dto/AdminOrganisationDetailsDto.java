package com.example.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrganisationDetailsDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String ownerName;
    private boolean active;
    private LocalDateTime createdAt;
    
    private long productsCount;
    private long activeProductsCount;
    
    private List<AdminProductDto> products;
}