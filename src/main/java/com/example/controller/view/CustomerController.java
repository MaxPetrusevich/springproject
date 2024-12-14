package com.example.controller.view;

import com.example.controller.BaseController;
import com.example.dto.CustomerProductDto;
import com.example.dto.SubscriptionDto;
import com.example.dto.UserDto;
import com.example.entity.Product;
import com.example.enums.SubscriptionStatus;
import com.example.mapper.EntityMapper;
import com.example.service.ProductService;
import com.example.service.SubscriptionService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customer")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class CustomerController extends BaseController {

    private final ProductService productService;
    private final SubscriptionService subscriptionService;
    private final UserService userService;
    private final EntityMapper entityMapper;

    @GetMapping("/products")
    public String showProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @PageableDefault(size = 12, sort = "id") Pageable pageable,
            Model model
    ) {
        Page<Product> products = productService.findAllAvailableProducts(search, sort, pageable);
        Page<CustomerProductDto> productDtos = products.map(entityMapper::toCustomerProductDto);
        
        model.addAttribute("products", productDtos);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        
        return "customer/products";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        String email = getCurrentUserEmail();
        UserDto user = entityMapper.toDto(userService.findByEmail(email));
        model.addAttribute("user", user);
        return "customer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute UserDto userDto) {
        String email = getCurrentUserEmail();
        UserDto user = entityMapper.toDto(userService.findByEmail(email));
        Long userId = user.getId();
        userService.updateProfile(userId, userDto);
        return "redirect:/customer/profile";
    }

    @GetMapping("/subscriptions")
    public String showSubscriptions(@RequestParam(required = false) String status, Model model) {
        String email = getCurrentUserEmail();
        List<SubscriptionDto> subscriptions;
        
        if (status != null && !status.isEmpty()) {
            SubscriptionStatus subscriptionStatus = SubscriptionStatus.valueOf(status);
            subscriptions = subscriptionService.findByUserEmailAndStatus(email, subscriptionStatus);
        } else {
            subscriptions = subscriptionService.findByUserEmail(email);
        }
        
        model.addAttribute("subscriptions", subscriptions);
        return "customer/subscriptions";
    }
} 