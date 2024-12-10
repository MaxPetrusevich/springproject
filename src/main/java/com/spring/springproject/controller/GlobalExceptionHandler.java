package com.spring.springproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        log.error("Запрашиваемая сущность не найдена: {}", ex.getMessage());
        return buildErrorModelAndView(
            "Ошибка 404 - Не найдено",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        log.error("Отказано в доступе: {}", ex.getMessage());
        return buildErrorModelAndView(
            "Ошибка 403 - Доступ запрещен",
            "У вас нет прав для выполнения этой операции",
            HttpStatus.FORBIDDEN.value(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView handleDataIntegrityViolationException(DataIntegrityViolationException ex, 
                                                            HttpServletRequest request) {
        log.error("Ошибка целостности данных: {}", ex.getMessage());
        return buildErrorModelAndView(
            "Ошибка 400 - Некорректные данные",
            "Невозможно выполнить операцию. Проверьте корректность данных и отсутствие связанных записей",
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ModelAndView handleValidationExceptions(Exception ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        
        if (ex instanceof MethodArgumentNotValidException) {
            ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        } else if (ex instanceof BindException) {
            ((BindException) ex).getBindingResult().getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        }

        String errorMessage = errors.entrySet().stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.joining(", "));

        log.error("Ошибка валидации: {}", errorMessage);
        return buildErrorModelAndView(
            "Ошибка 400 - Некорректные данные",
            errorMessage,
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ModelAndView handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, 
                                                                HttpServletRequest request) {
        log.error("Ошибка преобразования типов: {}", ex.getMessage());
        return buildErrorModelAndView(
            "Ошибка 400 - Некорректные параметры",
            "Неверный формат параметра " + ex.getName(),
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex, 
                                                     HttpServletRequest request) {
        log.error("Некорректные аргументы: {}", ex.getMessage());
        return buildErrorModelAndView(
            "Ошибка 400 - Некорректные данные",
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Непредвиденная ошибка: ", ex);
        return buildErrorModelAndView(
            "Ошибка 500 - Внутренняя ошибка сервера",
            "Произошла непредвиденная ошибка. Пожалуйста, попробуйте позже или обратитесь к администратору",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getRequestURI()
        );
    }

    private ModelAndView buildErrorModelAndView(String title, String message, int status, String path) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("title", title);
        mav.addObject("message", message);
        mav.addObject("status", status);
        mav.addObject("path", path);
        mav.addObject("timestamp", java.time.LocalDateTime.now());
        return mav;
    }
}