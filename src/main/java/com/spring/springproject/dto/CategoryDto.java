package com.spring.springproject.dto;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
@ToString(exclude = {"types"})
public class CategoryDto {
    private Integer id;
    private String name;
    private Set<TypeDto> types;
}
