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
    private String name;
    @ToString.Exclude
    private Set<TypeDto> types;
}
