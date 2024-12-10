package com.spring.springproject.dto;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class LoginRequest {

    @NotBlank(message = "Идентификационный номер обязателен.")
    @Pattern(
            regexp = "^[0-9]{7}[A-Za-z0-9][0-9]{6}[A-Za-z0-9]$",
            message = "Некорректный формат идентификационного номера."
    )
    private String identifyNumber;

    @NotBlank(message = "Пароль обязателен.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Пароль должен содержать минимум 8 символов, включая заглавные и строчные буквы, цифры и спецсимволы."
    )
    private String password;
}
