package com.spring.springproject.dto;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    @EqualsAndHashCode.Include
    private Integer id;
    @EqualsAndHashCode.Exclude

    private String name;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<TypeDto> types;
}
