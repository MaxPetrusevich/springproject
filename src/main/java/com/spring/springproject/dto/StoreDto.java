package com.spring.springproject.dto;

import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreDto {
    @EqualsAndHashCode.Include
    private Integer id;
    @EqualsAndHashCode.Exclude
    private String name;
    @EqualsAndHashCode.Exclude
    private String address;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<TechniqueDto> techniques;
}
