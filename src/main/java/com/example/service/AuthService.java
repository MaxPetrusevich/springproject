package com.example.service;

import com.example.dto.RegisterRequest;
import com.example.dto.VerifyEmailRequest;
import com.example.entity.User;
import com.example.enums.UserRole;
import com.example.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    
    private RegisterRequest savedRequest;
    
    public void register(RegisterRequest request) throws BusinessException {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Пароли не совпадают");
        }
        
        if (userService.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email уже зарегистрирован");
        }
        
        this.savedRequest = request;
        
        emailVerificationService.generateAndSendVerificationCode(request.getEmail());
    }
    
    @Transactional
    public User verifyEmailAndCompleteRegistration(VerifyEmailRequest request) {
        if (!emailVerificationService.verifyCode(request.getEmail(), request.getVerificationCode())) {
            throw new BusinessException("Неверный код подтверждения");
        }
        
        if (savedRequest == null || !savedRequest.getEmail().equals(request.getEmail())) {
            throw new BusinessException("Данные регистрации не найдены");
        }
        try {
            User user = userService.create(
                savedRequest.getEmail(),
                savedRequest.getFirstName(),
                savedRequest.getLastName(),
                passwordEncoder.encode(savedRequest.getPassword()),
                UserRole.valueOf(savedRequest.getRole().toUpperCase())
            );
            
            savedRequest = null;
            return user;
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Некорректная роль пользователя");
        }
    }
}