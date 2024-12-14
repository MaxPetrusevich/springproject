package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add((request, response, handler, ex) -> {
            ModelAndView mav;
            if (ex instanceof AuthenticationException) {
                mav = new ModelAndView("error/401");
                mav.addObject("message", "Необходима авторизация");
                mav.addObject("status", 401);
            } else if (ex instanceof AccessDeniedException) {
                mav = new ModelAndView("error/403");
                mav.addObject("message", "Доступ запрещен");
                mav.addObject("status", 403);
            } else if (ex instanceof NoHandlerFoundException) {
                mav = new ModelAndView("error/404");
                mav.addObject("message", "Страница не найдена");
                mav.addObject("status", 404);
            } else {
                mav = new ModelAndView("error/500");
                mav.addObject("message", "Внутренняя ошибка сервера");
                mav.addObject("status", 500);
            }
            return mav;
        });
    }
} 