package com.example.controller.view;

import com.example.controller.BaseController;
import com.example.entity.User;
import com.example.service.PaymentService;
import com.example.service.SubscriptionService;
import com.example.service.UserService;
import com.example.enums.UserRole;
import com.example.dto.UserDto;
import com.example.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController extends BaseController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;
    private final EntityMapper mapper;

    @GetMapping
    public String list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean enabled,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        Page<UserDto> users = userService.findAll(search, role, enabled, pageable)
            .map(mapper::toDto);
            
        model.addAttribute("users", users);
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("search", search);
        model.addAttribute("selectedRole", role);
        model.addAttribute("enabled", enabled);
        return "admin/users/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", mapper.toDto(user));
        model.addAttribute("subscriptions", mapper.toSubscriptionDtos(subscriptionService.findByUserId(id)));
        model.addAttribute("payments", mapper.toPaymentDtos(paymentService.findByUserId(id)));
        return "admin/users/view";
    }

    @PostMapping("/{id}/block")
    public String block(@PathVariable Long id) {
        userService.blockUser(id);
        return "redirect:/admin/users/" + id;
    }

    @PostMapping("/{id}/unblock")
    public String unblock(@PathVariable Long id) {
        userService.unblockUser(id);
        return "redirect:/admin/users/" + id;
    }
} 