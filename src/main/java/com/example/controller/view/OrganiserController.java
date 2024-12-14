package com.example.controller.view;

import com.example.controller.BaseController;
import com.example.dto.*;
import com.example.entity.Organisation;
import com.example.entity.Product;
import com.example.entity.SubscriptionPlan;
import com.example.entity.User;
import com.example.mapper.EntityMapper;
import com.example.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/organiser")
@PreAuthorize("hasRole('ORGANIZER')")
@RequiredArgsConstructor
public class OrganiserController extends BaseController {

    private final OrganisationService organisationService;
    private final ProductService productService;
    private final SubscriptionPlanService subscriptionPlanService;
    private final UserService userService;
    private final StatisticsService statisticsService;
    private final EntityMapper mapper;
    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email);
        Long userId = user.getId();
        List<Organisation> organisations = organisationService.findByOwnerId(userId);
        List<OrganiserOrganisationDto> organisationDtos = organisations.stream()
            .map(org -> OrganiserOrganisationDto.builder()
                .id(org.getId())
                .name(org.getName())
                .active(org.getActive())
                .productsCount(productService.countByOrganisationId(org.getId()))
                .activeSubscriptionsCount(subscriptionService.countActiveByOrganisationId(org.getId()))
                .monthlyIncome(paymentService.getMonthlyRevenueByOrganisationId(org.getId()).doubleValue())
                .build())
            .collect(Collectors.toList());

        DashboardStatisticsDto statistics = DashboardStatisticsDto.builder()
            .totalRevenue(paymentService.getTotalRevenueByOrganiserId(userId).doubleValue())
            .monthlyRevenue(paymentService.getMonthlyRevenueByOrganiserId(userId).doubleValue())
            .activeSubscriptionsCount(subscriptionService.countActiveByOrganisationOwnerId(userId))
            .renewalRate(subscriptionService.getRenewalRateByOrganisationOwnerId(userId))
            .build();

        model.addAttribute("statistics", statistics);
        model.addAttribute("organisations", organisationDtos);
        
        return "organiser/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email);
        Long userId = user.getId();
        model.addAttribute("user", mapper.toDto(userService.findById(userId)));
        return "organiser/profile";
    }

    @GetMapping("/organisations")
    public String listOrganisations(Model model) {
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email);
        Long userId = user.getId();
        List<Organisation> organisations = organisationService.findByOwnerId(userId);
        
        List<OrganiserOrganisationDto> organisationDtos = organisations.stream()
            .map(org -> {
                OrganiserOrganisationDto dto = OrganiserOrganisationDto.builder()
                    .id(org.getId())
                    .name(org.getName())
                    .description(org.getDescription())
                    .ownerId(org.getOwner().getId())
                    .ownerName(org.getOwner().getFirstName() + " " + org.getOwner().getLastName())
                    .active(org.getActive())
                    .createdAt(org.getCreatedAt())
                    .productsCount(productService.countByOrganisationId(org.getId()))
                    .activeSubscriptionsCount(subscriptionService.countActiveByOrganisationId(org.getId()))
                    .totalSubscriptionsCount(subscriptionService.countByOrganisationId(org.getId()))
                    .monthlyIncome(paymentService.getMonthlyRevenueByOrganisationId(org.getId()).doubleValue())
                    .totalIncome(paymentService.getTotalRevenueByOrganisationId(org.getId()).doubleValue())
                    .build();
                return dto;
            })
            .collect(Collectors.toList());

        model.addAttribute("organisations", organisationDtos);
        return "organiser/organisations/list";
    }

    @GetMapping("/organisations/{id}")
    public String viewOrganisation(@PathVariable Long id, Model model) {
        checkOrganisationAccess(id);
        Organisation org = organisationService.findById(id);
        
        List<OrganiserProductDto> products = productService.findByOrganisationId(id).stream()
            .map(product -> OrganiserProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .active(product.isActive())
                .organisationId(product.getOrganisation().getId())
                .subscriptionPlansCount(subscriptionPlanService.countByProductId(product.getId()))
                .activeSubscriptionsCount(subscriptionService.countActiveByProductId(product.getId()))
                .build())
            .collect(Collectors.toList());

        OrganisationDetailsDto details = OrganisationDetailsDto.builder()
            .id(org.getId())
            .name(org.getName())
            .description(org.getDescription())
            .ownerId(org.getOwner().getId())
            .ownerName(org.getOwner().getFirstName() + " " + org.getOwner().getLastName())
            .active(org.getActive())
            .createdAt(org.getCreatedAt())
            .productsCount(productService.countByOrganisationId(id))
            .activeProductsCount(productService.countActiveByOrganisationId(id))
            .subscriptionPlansCount(subscriptionPlanService.countByOrganisationId(id))
            .activeSubscriptionPlansCount(subscriptionPlanService.countActiveByOrganisationId(id))
            .activeSubscriptionsCount(subscriptionService.countActiveByOrganisationId(id))
            .totalSubscriptionsCount(subscriptionService.countByOrganisationId(id))
            .monthlyIncome(paymentService.getMonthlyRevenueByOrganisationId(id).doubleValue())
            .totalIncome(paymentService.getTotalRevenueByOrganisationId(id).doubleValue())
            .products(products)
            .build();

        model.addAttribute("organisation", details);
        model.addAttribute("products", products);
        return "organiser/organisations/view";
    }

    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long organisationId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email);
        Long userId = user.getId();
        
        Page<OrganiserProductListDto> products = productService.findByOwnerId(userId, search, organisationId, pageable)
            .map(product -> OrganiserProductListDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .active(product.isActive())
                .organisationId(product.getOrganisation().getId())
                .organisationName(product.getOrganisation().getName())
                .subscriptionPlansCount(subscriptionPlanService.countByProductId(product.getId()))
                .activeSubscriptionsCount(subscriptionService.countActiveByProductId(product.getId()))
                .monthlyIncome(paymentService.getMonthlyRevenueByProductId(product.getId()).doubleValue())
                .totalIncome(paymentService.getTotalRevenueByProductId(product.getId()).doubleValue())
                .build()
            );
        
        model.addAttribute("products", products);
        model.addAttribute("organisations", organisationService.findByOwnerId(userId));
        model.addAttribute("search", search);
        model.addAttribute("selectedOrganisation", organisationId);
        
        model.addAttribute("totalProducts", products.getTotalElements());
        model.addAttribute("activeProducts", products.getContent().stream().filter(OrganiserProductListDto::isActive).count());
        model.addAttribute("totalIncome", products.getContent().stream().mapToDouble(OrganiserProductListDto::getTotalIncome).sum());
        model.addAttribute("monthlyIncome", products.getContent().stream().mapToDouble(OrganiserProductListDto::getMonthlyIncome).sum());
        
        return "organiser/products/list";
    }

    @GetMapping("/subscription-plans")
    public String subscriptionPlans(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long productId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email);
        Long userId = user.getId();
        
        Page<OrganiserSubscriptionPlanDto> plans = subscriptionPlanService.findByOwnerId(userId, search, productId, pageable)
            .map(plan -> OrganiserSubscriptionPlanDto.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .active(plan.isActive())
                .price(plan.getPrice())
                .periodDays(plan.getPeriodDays())
                .createdAt(plan.getCreatedAt())
                .productId(plan.getProduct().getId())
                .productName(plan.getProduct().getName())
                .organisationName(plan.getProduct().getOrganisation().getName())
                .activeSubscriptionsCount(subscriptionService.countActiveByPlanId(plan.getId()))
                .totalSubscriptionsCount(subscriptionService.countByPlanId(plan.getId()))
                .renewalRate(subscriptionService.getRenewalRate(plan.getId()))
                .monthlyIncome(paymentService.getMonthlyRevenueByPlanId(plan.getId()).doubleValue())
                .totalIncome(paymentService.getTotalRevenueByPlanId(plan.getId()).doubleValue())
                .newSubscriptionsThisMonth(subscriptionService.countNewSubscriptionsThisMonth(plan.getId()))
                .build()
            );

        model.addAttribute("plans", plans);
        model.addAttribute("products", productService.findByOrganiserId(userId));
        model.addAttribute("search", search);
        model.addAttribute("selectedProduct", productId);
        
        model.addAttribute("totalPlans", plans.getTotalElements());
        model.addAttribute("activePlans", plans.getContent().stream().filter(OrganiserSubscriptionPlanDto::isActive).count());
        model.addAttribute("totalIncome", plans.getContent().stream().mapToDouble(OrganiserSubscriptionPlanDto::getTotalIncome).sum());
        model.addAttribute("monthlyIncome", plans.getContent().stream().mapToDouble(OrganiserSubscriptionPlanDto::getMonthlyIncome).sum());
        
        return "organiser/subscription-plans/list";
    }

    @GetMapping("/organisations/create")
    public String createOrganisation(Model model) {
        model.addAttribute("organisation", new OrganisationDto());
        return "organiser/organisations/form";
    }

    @GetMapping("/organisations/{id}/edit")
    public String editOrganisation(@PathVariable Long id, Model model) {
        checkOrganisationAccess(id);
        model.addAttribute("organisation", mapper.toOrganisationDto(organisationService.findById(id)));
        return "organiser/organisations/form";
    }

    @GetMapping("/products/create")
    public String createProduct(Model model, @RequestParam(required = false) Long organisationId) {
        model.addAttribute("product", new ProductDto());
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email);
        Long userId = user.getId();
        if(organisationId != null) model.addAttribute("organisationId", organisationId);
        model.addAttribute("organisations", organisationService.findByOwnerId(userId));
        return "organiser/products/form";
    }

    @GetMapping("/products/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        checkOrganisationAccess(product.getOrganisation().getId());
        
        OrganiserProductDto productDto = OrganiserProductDto.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .active(product.isActive())
            .organisationId(product.getOrganisation().getId())
            .organisationName(product.getOrganisation().getName())
            .createdAt(product.getCreatedAt())
            .subscriptionPlansCount(subscriptionPlanService.countByProductId(product.getId()))
            .activeSubscriptionsCount(subscriptionService.countActiveByProductId(product.getId()))
            .monthlyIncome(paymentService.getMonthlyRevenueByProductId(product.getId()).doubleValue())
            .totalIncome(paymentService.getTotalRevenueByProductId(product.getId()).doubleValue())
            .build();

        model.addAttribute("product", productDto);
        model.addAttribute("statistics", statisticsService.getProductStatistics(id));
        model.addAttribute("subscriptionPlans", 
            mapper.toPlanDtos(subscriptionPlanService.findByProductId(id)));
        return "organiser/products/view";
    }

    @GetMapping("/products/{id}/edit")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        checkOrganisationAccess(product.getOrganisation().getId());
        
        model.addAttribute("product", mapper.toDto(product));
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email);
        Long userId = user.getId();
        model.addAttribute("organisations", organisationService.findByOwnerId(userId));
        return "organiser/products/form";
    }

    @GetMapping("/subscription-plans/create")
    public String createSubscriptionPlan(
            @RequestParam(required = false) Long productId,
            Model model
    ) {
        SubscriptionPlanDto plan = new SubscriptionPlanDto();
        plan.setProductId(productId);
        
        model.addAttribute("plan", plan);
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email);
        Long userId = user.getId();
        model.addAttribute("products", productService.findByOrganiserId(userId));
        return "organiser/subscription-plans/form";
    }

    @GetMapping("/subscription-plans/{id}")
    public String viewSubscriptionPlan(@PathVariable Long id, Model model) {
        SubscriptionPlan plan = subscriptionPlanService.getPlan(id);
        checkOrganisationAccess(plan.getProduct().getOrganisation().getId());
        
        model.addAttribute("plan", mapper.toDto(plan));
        model.addAttribute("statistics", statisticsService.getPlanStatistics(id));
        return "organiser/subscription-plans/view";
    }

    @GetMapping("/subscription-plans/{id}/edit")
    public String editSubscriptionPlan(@PathVariable Long id, Model model) {
        SubscriptionPlan plan = subscriptionPlanService.getPlan(id);
        checkOrganisationAccess(plan.getProduct().getOrganisation().getId());
        
        model.addAttribute("plan", mapper.toDto(plan));
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email);
        Long userId = user.getId();
        model.addAttribute("products", productService.findByOrganiserId(userId));
        return "organiser/subscription-plans/form";
    }

    @GetMapping("/organisations/{organisationId}/products/create")
    public String showCreateProductForm(@PathVariable Long organisationId, Model model) {
        model.addAttribute("product", new ProductDto());
        model.addAttribute("organisationId", organisationId);  // Добавляем ID организации
        return "organiser/products/form";
    }

    @GetMapping("/products/{productId}/subscription-plans/create")
    public String showCreateSubscriptionPlanForm(@PathVariable Long productId, Model model) {
        model.addAttribute("plan", new SubscriptionPlanDto());
        model.addAttribute("productId", productId);  // Передаем ID продукта
        return "organiser/subscription-plans/form";
    }

    private void checkOrganisationAccess(Long organisationId) {
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email);
        Long userId = user.getId();
        if (!organisationService.isOwner(organisationId, userId)) {
            throw new AccessDeniedException("У вас нет доступа к этой организации");
        }
    }
} 