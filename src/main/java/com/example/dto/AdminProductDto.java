package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductDto {
    private Long id;
    private String name;
    private String description;
    private boolean active;
    private Long organisationId;
}