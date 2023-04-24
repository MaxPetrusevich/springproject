package com.spring.springproject.dto;

import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
public class StoreDto {
    private Integer id;
    private String name;
    private String address;
    private Set<TechniqueDto> techniques;
}
