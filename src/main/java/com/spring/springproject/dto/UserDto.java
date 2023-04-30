package com.spring.springproject.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @EqualsAndHashCode.Include
    private Integer id;
    private String name;
    private String email;
}