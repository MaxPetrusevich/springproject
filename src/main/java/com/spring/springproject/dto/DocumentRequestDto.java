package com.spring.springproject.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class DocumentRequestDto {

    private Long id;

    @NotBlank(message = "Название документа обязательно")
    @Size(max = 255, message = "Название документа не должно превышать 255 символов")
    private String name;

    private LocalDateTime loadingDate;

    @NotNull(message = "Тип документа обязателен")
    private Long typeId;

    @NotNull(message = "Заявка обязательна")
    private Long bidId;

    @NotNull(message = "Файл обязателен")
    private MultipartFile file;

    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;

    // Дополнительные поля для метаданных файла
    private String mimeType;
    private Long fileSize;
    private String originalFilename;
    private String hash;
}