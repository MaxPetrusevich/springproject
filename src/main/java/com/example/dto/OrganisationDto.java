package com.example.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrganisationDto {
    private Long id;
    private String name;
    private String description;
    private String contactEmail;
    private String phone;
    private String address;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;
    private boolean active;
    private List<ProductDto> products;
    private OrganisationStatisticsDto statistics;
    private String ownerEmail;
    private long subscribersCount;
} 