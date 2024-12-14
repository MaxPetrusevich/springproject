package com.example.controller.view;

import com.example.controller.BaseController;
import com.example.dto.AdminSubscriptionDto;
import com.example.dto.SubscriptionDetailsDto;
import com.example.entity.Product;
import com.example.entity.Subscription;
import com.example.entity.SubscriptionPlan;
import com.example.enums.SubscriptionStatus;
import com.example.enums.UserRole;
import com.example.mapper.EntityMapper;
import com.example.service.PaymentService;
import com.example.service.ProductService;
import com.example.service.SubscriptionService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/subscriptions")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminSubscriptionController extends BaseController {

    private final SubscriptionService subscriptionService;
    private final UserService userService;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final EntityMapper mapper;

    @GetMapping
    public String list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) SubscriptionStatus status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long productId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        Page<AdminSubscriptionDto> subscriptions = subscriptionService.findAll(search, status, userId, productId, pageable)
            .map(subscription -> AdminSubscriptionDto.builder()
                    .id(subscription.getId())
                    .status(subscription.getStatus().name())
                    .userEmail(subscription.getUser().getEmail())
                    .endDate(subscription.getEndDate())
                    .price(subscription.getPlan().getPrice())
                    .productId(subscription.getPlan().getProduct().getId())
                    .planName(subscription.getPlan().getName())
                    .productName(subscription.getPlan().getProduct().getName())
                    .userId(subscription.getUser().getId())
                    .planId(subscription.getPlan().getId())
                    .startDate(subscription.getStartDate())
                    .build());

        model.addAttribute("subscriptions", subscriptions);
        model.addAttribute("statuses", SubscriptionStatus.values());
        model.addAttribute("users", userService.findByRole(UserRole.USER));
        model.addAttribute("products", productService.findAllProducts());
        model.addAttribute("search", search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedUser", userId);
        model.addAttribute("selectedProduct", productId);
        return "admin/subscriptions/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Subscription subscription = subscriptionService.findById(id);
        SubscriptionDetailsDto dto = new SubscriptionDetailsDto();
        
        // Маппинг основных полей
        dto.setId(subscription.getId());
        dto.setStatus(subscription.getStatus());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setCreatedAt(subscription.getCreatedAt());
        
        // Информация о пользователе
        dto.setUserId(subscription.getUser().getId());
        dto.setUserEmail(subscription.getUser().getEmail());
        dto.setUserName(subscription.getUser().getFirstName() + " " + subscription.getUser().getLastName());
        
        // Информация о плане и продукте
        SubscriptionPlan plan = subscription.getPlan();
        dto.setPlanId(plan.getId());
        dto.setPlanName(plan.getName());
        dto.setPlanPrice(plan.getPrice());
        dto.setPlanPeriodDays(plan.getPeriodDays());
        
        Product product = plan.getProduct();
        dto.setProductId(product.getId());
        dto.setProductName(product.getName());
        dto.setOrganisationId(product.getOrganisation().getId());
        dto.setOrganisationName(product.getOrganisation().getName());
        
        // Платежи
        dto.setPayments(paymentService.findAllBySubscriptionId(id).stream()
            .map(mapper::toPaymentDto)
            .collect(Collectors.toList()));
        
        model.addAttribute("subscription", dto);
        return "admin/subscriptions/view";
    }

    @PostMapping("/{id}/cancel")
    public String cancelSubscription(@PathVariable Long id) {
        subscriptionService.cancelSubscription(id);
        return "redirect:/admin/subscriptions/" + id;
    }

    @PostMapping("/{id}/renew")
    public String renewSubscription(@PathVariable Long id) {
        subscriptionService.renewSubscription(id);
        return "redirect:/admin/subscriptions/" + id;
    }
} 