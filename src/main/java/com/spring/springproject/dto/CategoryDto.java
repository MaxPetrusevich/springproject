package com.spring.springproject.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Set<TypeDto> types;
}
