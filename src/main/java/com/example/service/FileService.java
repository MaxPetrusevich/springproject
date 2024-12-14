package com.example.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${app.upload.avatar-dir}")
    private String avatarUploadDir;

    @Value("${app.upload.avatar-url-prefix}")
    private String avatarUrlPrefix;

    public String saveAvatar(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(avatarUploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        
        Files.copy(file.getInputStream(), filePath);
        
        return filename;
    }

    public void deleteAvatar(String avatarUrl) {
        if (avatarUrl != null && avatarUrl.startsWith(avatarUrlPrefix)) {
            String filename = avatarUrl.substring(avatarUrlPrefix.length() + 1);
            try {
                Path filePath = Paths.get(avatarUploadDir, filename);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Resource loadAvatar(String filename) {
        try {
            Path file = Paths.get(avatarUploadDir).resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
} 