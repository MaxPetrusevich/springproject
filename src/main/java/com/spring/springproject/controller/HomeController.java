package com.spring.springproject.controller;

import com.spring.springproject.dto.LoginRequest;
import com.spring.springproject.dto.UserRegisterDto;
import com.spring.springproject.service.impl.CitizenService;
import com.spring.springproject.service.impl.UserService;
import com.spring.springproject.validation.EmailValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UserService userService;
    private final CitizenService citizenService;
    private final AuthenticationManager authenticationManager;
    private final EmailValidator emailValidator;

    @GetMapping("/")
    public String home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Если пользователь не аутентифицирован или это анонимный пользователь
        if (auth == null || auth.getPrincipal() instanceof String || 
            "anonymousUser".equals(auth.getPrincipal())) {
            return "landing";
        }

        // Если пользователь аутентифицирован, проверяем его роль
        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin";
        }
        return "redirect:/customer/services";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверный ИИН или пароль");
        }
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }
    @PostMapping("/login")
    public String login(@RequestParam String identifyNumber,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(identifyNumber, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Определяем роль пользователя и перенаправляем
            return "redirect:/";

        } catch (AuthenticationException e) {
            redirectAttributes.addFlashAttribute("error", "Неверный ИИН или пароль");
            return "redirect:/login?error";
        }
    }
    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new UserRegisterDto());
        }
        return "registration/register";
    }

    @PostMapping("/registration")
    public String register(@Valid @ModelAttribute("registrationForm") UserRegisterDto registerDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registrationForm", 
                                              bindingResult);
            redirectAttributes.addFlashAttribute("registrationForm", registerDto);
            return "redirect:/registration";
        }

        if (!emailValidator.verifyCode(registerDto.getEmail(), registerDto.getEmailCode())) {
            bindingResult.rejectValue("emailCode", "invalid.code", "Неверный код подтверждения email");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registrationForm", 
                                              bindingResult);
            redirectAttributes.addFlashAttribute("registrationForm", registerDto);
            return "redirect:/registration";
        }

        try {
            userService.register(registerDto);
            redirectAttributes.addFlashAttribute("successMessage", "Регистрация успешно завершена");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("registrationForm", registerDto);
            return "redirect:/registration";
        }
    }

    @PostMapping("/registration/validate-email")
    @ResponseBody
    public Map<String, Object> validateEmail(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        if (userService.isEmailTaken(email)) {
            response.put("success", false);
            response.put("message", "Email уже используется");
            return response;
        }

        Integer code = emailValidator.sendVerificationCode(email);
        if (code != null) {
            response.put("success", true);
            response.put("message", "Код подтверждения отправлен на email");
        } else {
            response.put("success", false);
            response.put("message", "Ошибка отправки кода подтверждения");
        }
        return response;
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";

    }@GetMapping("/unauth-contact")
    public String unauthContact() {
        return "unauth-contacts";
    }

}
