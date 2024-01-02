package com.spring.springproject.dto;

import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @EqualsAndHashCode.Include
    private Integer id;
    @EqualsAndHashCode.Exclude
    private String username;

    @EqualsAndHashCode.Exclude
    private String password;
    @EqualsAndHashCode.Exclude
    private String image;
    @EqualsAndHashCode.Exclude
    private String name;
    @EqualsAndHashCode.Exclude
    private String surname;
    @EqualsAndHashCode.Exclude
    private String email;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<OrderDto> orders;

}