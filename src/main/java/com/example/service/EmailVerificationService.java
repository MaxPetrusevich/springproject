package com.example.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    
    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private static final long VERIFICATION_CODE_TTL = 15;
    
    public String generateAndSendVerificationCode(String email) {
        String verificationCode = generateVerificationCode();
        
        redisTemplate.opsForValue().set(
            getRedisKey(email),
            verificationCode,
            VERIFICATION_CODE_TTL,
            TimeUnit.MINUTES
        );
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Код подтверждения регистрации");
        message.setText("Ваш код подтверждения: " + verificationCode);
        mailSender.send(message);
        
        return verificationCode;
    }
    
    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(getRedisKey(email));
        return code != null && code.equals(storedCode);
    }
    
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
    
    private String getRedisKey(String email) {
        return "verification:" + email;
    }
}