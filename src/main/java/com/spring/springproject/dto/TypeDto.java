package com.spring.springproject.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
@ToString(exclude = {"category "})
public class TypeDto {
    private Integer id;
    private String name;
    private CategoryDto category;
}
