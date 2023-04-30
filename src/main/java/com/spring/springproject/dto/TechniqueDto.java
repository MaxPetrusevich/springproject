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
    private Double price;
    @ToString.Exclude
    private ModelDto model;
    @ToString.Exclude
    private Set<StoreDto> storeList;
    @ToString.Exclude
    private ProducerDto producer;
    @ToString.Exclude
    private CategoryDto category;
}
