package com.example.controller;

import com.example.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {
    
    protected String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
} 