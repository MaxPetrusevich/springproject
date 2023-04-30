package com.spring.springproject.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProducerDto {
    @EqualsAndHashCode.Include
    private Integer id;
    private String name;
    private String country;
}
