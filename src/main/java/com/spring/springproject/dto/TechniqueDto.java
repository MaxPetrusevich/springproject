package com.spring.springproject.dto;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechniqueDto {
    @EqualsAndHashCode.Include
    private Integer id;
    @EqualsAndHashCode.Exclude
    private Double price;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ModelDto model;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<StoreDto> storeList;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ProducerDto producer;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private CategoryDto category;
}
