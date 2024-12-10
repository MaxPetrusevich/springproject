package com.spring.springproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class CitizenRequestDto {
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    private String middleName;
    
    @Pattern(regexp = "\\+?[0-9]{11,12}")
    private String phone;
    
    @Email
    private String email;
    
    @NotBlank
    @Pattern(regexp = "[0-9]{12}")
    private String identifyNumber;
    
    @NotBlank
    private String passportSeries;
    
    @NotBlank
    private String passportNumber;
    
    private String address;
    
    private Long userId;
    
    @NotNull(message = "Код подтверждения email обязателен")
    private Integer emailCode;
}