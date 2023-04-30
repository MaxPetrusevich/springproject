package com.spring.springproject.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelDto {
    @EqualsAndHashCode.Include
    private Integer id;
    private String name;
}
