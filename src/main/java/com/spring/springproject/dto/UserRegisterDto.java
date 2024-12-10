package com.spring.springproject.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserRegisterDto {
    @NotBlank(message = "ИИН обязателен")
    @Size(min = 12, max = 12, message = "ИИН должен содержать 12 цифр")
    private String identifyNumber;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    private String password;

    @NotBlank(message = "Подтверждение пароля обязательно")
    private String confirmPassword;

    // Данные гражданина
    @NotBlank(message = "Имя обязательно")
    @Pattern(regexp = "^[А-ЯЁ][а-яё]+$", message = "Имя должно быть написано кириллицей и начинаться с заглавной буквы")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Pattern(regexp = "^[А-ЯЁ][а-яё]+$", message = "Фамилия должна быть написана кириллицей и начинаться с заглавной буквы")
    private String lastName;

    @Pattern(regexp = "^$|^[А-ЯЁ][а-яё]+$", message = "Отчество должно быть написано кириллицей и начинаться с заглавной буквы")
    private String middleName;

    @NotBlank(message = "Телефон обязателен")
    @Pattern(regexp = "^\\+375\\((17|29|33|44|25)\\)\\d{3}-\\d{2}-\\d{2}$", 
            message = "Телефон должен быть в формате +375(XX)XXX-XX-XX")
    private String phone;

    @NotBlank(message = "Серия паспорта обязательна")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Серия паспорта должна состоять из 2 заглавных букв")
    private String passportSeries;

    @NotBlank(message = "Номер паспорта обязателен")
    @Pattern(regexp = "^\\d{7}$", message = "Номер паспорта должен состоять из 7 цифр")
    private String passportNumber;

    @NotBlank(message = "Адрес обязателен")
    @Size(min = 10, max = 255, message = "Адрес должен быть от 10 до 255 символов")
    private String address;

    private Integer emailCode;
}
