package com.spring.springproject.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeDto {
    @EqualsAndHashCode.Include
    private Integer id;
    @EqualsAndHashCode.Exclude
    private String name;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private CategoryDto category;
}
