package com.example.controller;

import com.example.dto.PasswordChangeRequest;
import com.example.dto.UserDto;
import com.example.entity.User;
import com.example.mapper.EntityMapper;
import com.example.service.FileService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController extends BaseController {
    private final UserService userService;
    private final EntityMapper entityMapper;
    private final FileService fileService;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getProfile() {
        UserDto user = entityMapper.toDto(userService.findById(getCurrentUserId()));
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> updateProfile(@RequestBody UserDto userDto) {
        UserDto updated = entityMapper.toDto(userService.updateProfile(getCurrentUserId(), userDto));
        return ResponseEntity.ok(updated);
    }


    @PostMapping("/profile/avatar")
    public ResponseEntity<UserDto> updateAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String email = getCurrentUserEmail();
            User user = userService.findByEmail(email);
            Long userId = user.getId();
            String avatarUrl = fileService.saveAvatar(file);
            UserDto updatedUser = entityMapper.toDto(userService.updateAvatar(userId, avatarUrl));
            return ResponseEntity.ok(updatedUser);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping("/profile/password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request) {
        try {
            String email = getCurrentUserEmail();
            User user = userService.findByEmail(email);

            // Проверяем текущий пароль
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Текущий пароль неверен"));
            }

            // Проверяем совпадение паролей
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Пароли не совпадают"));
            }

            userService.updatePassword(user.getId(), request.getNewPassword());

            return ResponseEntity.ok(Map.of("message", "Пароль успешно изменен"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Ошибка при смене пароля"));
        }
    }
} 