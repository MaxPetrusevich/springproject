package com.spring.springproject.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
public class ProducerDto {
    private Integer id;
    private String name;
    private String country;
}
