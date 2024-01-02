package com.spring.springproject.controller;

import com.spring.springproject.email.EmailSender;
import com.spring.springproject.entities.Role;
import com.spring.springproject.entities.User;
import com.spring.springproject.repositories.RoleRepository;
import com.spring.springproject.repositories.UserRepository;
import com.spring.springproject.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.Set;

import static com.spring.springproject.controller.Constants.*;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private Integer generatedCode;
    private User user;
    private Role roleRegister;

    @GetMapping(START_PAGE)
    public String home() {
        return HOME_PAGE;
    }

    @GetMapping("/login")
    public String login(@RequestParam(name = "error", defaultValue = "false", required = false) String error, Model model) {
        model.addAttribute("error", Boolean.parseBoolean(error));
        return "login";
    }

    @GetMapping("/registration")
    public String register() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@RequestParam("firstname") String firstname,
                               @RequestParam("surname") String surname,
                               @RequestParam("email") String email,
                               @RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("confirm_password") String confirmPassword, Model model) {
        if (password.equals(confirmPassword)) {
            user = User.builder()
                    .name(firstname)
                    .surname(surname)
                    .email(email)
                    .userName(username)
                    .password(new BCryptPasswordEncoder().encode(password))
                    .build();
            generatedCode = new EmailSender().sendMail(email);
            return "registrationConfirm";
        } else {
            return "redirect:/registration";
        }

    }

    @PostMapping("/registration/confirm")
    public String confirm(@RequestParam("code") String code) {
        if (code.equals(String.valueOf(generatedCode))) {
            Set<Role> roles = new HashSet<>();
            roleRegister = roleRepository.findByName("USER");
            if (roleRegister != null) {
                roles.add(roleRegister);
            } else {
                roleRegister = new Role();
                roleRegister.setName("USER");
                roleRepository.save(roleRegister);
            }
            user.setRoles(roles);
            userRepository.saveAndFlush(user);
            return "redirect:/login";
        } else {
            return "redirect:/registration";
        }
    }

    @GetMapping("/admin/home")
    public String goHome() {
        return HOME_PAGE;
    }

    @GetMapping("/admin/home/redirect")
    public String view(@RequestParam(LIST_NAME) String listName) {
        int choose = Integer.parseInt(listName);
        String address = null;
        switch (choose) {
            case 1:
                address = REDIRECT + TECHNIQUES;
                break;
            case 2:
                address = REDIRECT + CATEGORIES_URL;
                break;
            case 3:
                address = REDIRECT + MODELS_URL;
                break;
            case 4:
                address = REDIRECT + PRODUCERS_URL;
                break;
            case 5:
                address = REDIRECT + TYPES_URL;
                break;
            case 6:
                address = REDIRECT + STORES_URL;
                break;
            case 7:
                address = REDIRECT + "/admin/users";
        }
        return address;
    }
}
