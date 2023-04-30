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
    private String name;
    private String address;
    @ToString.Exclude
    private Set<TechniqueDto> techniques;
}
