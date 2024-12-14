package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    
    @GetMapping("/error")
    public String handleError(
            @RequestParam(value = "message", required = false) String message,
            Model model
    ) {
        if (message != null) {
            model.addAttribute("message", message);
        }
        return "error/error";
    }
} 