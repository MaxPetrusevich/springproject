package com.spring.springproject.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderTechniqueDto {

    private Integer id;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OrderDto order;
    @ToString.Exclude
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private TechniqueDto technique;

    private Integer quantity;
}
