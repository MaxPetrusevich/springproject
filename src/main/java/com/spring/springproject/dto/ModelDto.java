package com.spring.springproject.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
public class ModelDto {
    private Integer id;
    private String name;
}
