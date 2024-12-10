package com.spring.springproject.validation;

import com.spring.springproject.email.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailValidator {
    private final EmailSender emailSender;
    private final Map<String, Integer> emailCodes = new HashMap<>();

    public Integer sendVerificationCode(String email) {
        Integer code = emailSender.sendMail(email);
        if (code != 0 && code != 1) {
            emailCodes.put(email, code);
            return code;
        }
        return null;
    }

    public boolean verifyCode(String email, Integer code) {
        Integer storedCode = emailCodes.get(email);
        if (storedCode != null && storedCode.equals(code)) {
            emailCodes.remove(email);
            return true;
        }
        return false;
    }
} 