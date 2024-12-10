package com.spring.springproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String password;
    
    private Long roleId;
    
    @Size(min = 12, max = 12, message = "ИИН должен содержать 12 символов")
    private String identifyNumber;
} 