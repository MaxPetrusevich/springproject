package com.spring.springproject.util;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileUtils {
    
    public static boolean isValidFileType(MultipartFile file, List<String> allowedTypes) {
        String contentType = file.getContentType();
        return contentType != null && allowedTypes.contains(contentType.toLowerCase());
    }

    public static String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    public static String sanitizeFilename(String filename) {
        if (filename == null) {
            return null;
        }
        // Удаляем все небезопасные символы
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
} 