package com.spring.springproject.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProducerDto {
    @EqualsAndHashCode.Include
    private Integer id;
    @EqualsAndHashCode.Exclude

    private String name;
    @EqualsAndHashCode.Exclude

    private String country;
}
