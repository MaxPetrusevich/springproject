package com.spring.springproject.dto;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
public class TechniqueDto {
    private Integer id;
    private Double price;
    private ModelDto model;
    private Set<StoreDto> storeList;
    private ProducerDto producer;
    private CategoryDto category;
}
