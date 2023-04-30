package com.spring.springproject.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeDto {
    @EqualsAndHashCode.Include
    private Integer id;
    private String name;
    @ToString.Exclude
    private CategoryDto category;
}
