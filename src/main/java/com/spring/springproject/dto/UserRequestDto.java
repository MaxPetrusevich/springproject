package com.spring.springproject.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class UserRequestDto {
    private Long id;
    @NotBlank(message = "ИИН обязателен")
    @Pattern(regexp = "[0-9]{12}", message = "ИИН должен содержать 12 цифр")
    private String identifyNumber;
    
    private String password;
    
    @NotNull(message = "Роль обязательна")
    private Long roleId;
    
    private MultipartFile avatarFile;
}