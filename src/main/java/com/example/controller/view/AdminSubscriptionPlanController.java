package com.example.controller.view;

import com.example.controller.BaseController;
import com.example.dto.SubscriptionPlanDto;
import com.example.entity.SubscriptionPlan;
import com.example.mapper.EntityMapper;
import com.example.service.ProductService;
import com.example.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/subscription-plans")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminSubscriptionPlanController extends BaseController {

    private final SubscriptionPlanService subscriptionPlanService;
    private final ProductService productService;
    private final EntityMapper mapper;

    @GetMapping
    public String list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long productId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        Page<SubscriptionPlanDto> plans = subscriptionPlanService.findAllPlans(search, productId, pageable)
            .map(mapper::toDto);
            
        model.addAttribute("plans", plans);
        model.addAttribute("products", productService.findAllProducts());
        model.addAttribute("search", search);
        model.addAttribute("selectedProduct", productId);
        return "admin/subscription-plans/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        SubscriptionPlan plan = subscriptionPlanService.getPlan(id);
        model.addAttribute("plan", mapper.toDto(plan));
        model.addAttribute("product", mapper.toDto(plan.getProduct()));
        model.addAttribute("subscriptions", mapper.toSubscriptionDtos(plan.getSubscriptions()));
        return "admin/subscription-plans/view";
    }

    @GetMapping("/create")
    public String createForm(@RequestParam Long productId, Model model) {
        SubscriptionPlanDto plan = new SubscriptionPlanDto();
        plan.setProductId(productId);
        plan.setActive(true);
        
        model.addAttribute("plan", plan);
        model.addAttribute("product", productService.findById(productId));
        return "admin/subscription-plans/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        SubscriptionPlan plan = subscriptionPlanService.getPlan(id);
        model.addAttribute("plan", mapper.toDto(plan));
        model.addAttribute("product", mapper.toDto(plan.getProduct()));
        return "admin/subscription-plans/form";
    }

    @PostMapping
    public String create(@ModelAttribute SubscriptionPlanDto planDto) {
        subscriptionPlanService.createPlan(planDto);
        return "redirect:/admin/products/" + planDto.getProductId();
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute SubscriptionPlanDto planDto) {
        subscriptionPlanService.updatePlan(id, planDto);
        return "redirect:/admin/products/" + planDto.getProductId();
    }
} 