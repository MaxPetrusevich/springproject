package com.example.controller.view;

import com.example.controller.BaseController;
import com.example.dto.*;
import com.example.dto.ProductStatisticsDto;
import com.example.dto.stats.*;
import com.example.entity.*;
import com.example.mapper.EntityMapper;
import com.example.service.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
    private final RecommendationService recommendationService;
    private static final Logger log = LoggerFactory.getLogger(OrganiserController.class);

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
    public String organisations(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        try {
            String email = getCurrentUserEmail();
            User user = userService.findByEmail(email);
            Long userId = user.getId();

            Page<Organisation> organisationsPage = organisationService.findByOwnerId(userId, search, active, pageable);

            Page<OrganiserOrganisationDto> organisations = organisationsPage.map(org ->
                    OrganiserOrganisationDto.builder()
                            .id(org.getId())
                            .name(org.getName())
                            .description(org.getDescription())
                            .active(org.getActive())
                            .productsCount(productService.countByOrganisationId(org.getId()))
                            .activeSubscriptionsCount(subscriptionService.countActiveByOrganisationId(org.getId()))
                            .build()
            );

            model.addAttribute("organisations", organisations);
            model.addAttribute("search", search);
            model.addAttribute("active", active);

            return "organiser/organisations/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("organisations", Page.empty(pageable));
            model.addAttribute("search", search);
            model.addAttribute("active", active);
            model.addAttribute("error", "Произошла ошибка при загрузке списка организаций");
            return "organiser/organisations/list";
        }
    }

    @GetMapping("/organisations/{id}")
    public String viewOrganisation(@PathVariable Long id, Model model) {
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email);
        Long userId = user.getId();
        
        if (!organisationService.isOwner(id, userId)) {
            throw new AccessDeniedException("У вас нет доступа к этой организации");
        }
        
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

        Page<Product> productsPage = productService.findByOrganiserId(userId, search, organisationId, pageable);
        
        Page<OrganiserProductListDto> products = productsPage.map(product ->
            OrganiserProductListDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .active(product.isActive())
                .organisationId(product.getOrganisation().getId())
                .organisationName(product.getOrganisation().getName())
                .subscriptionPlansCount(subscriptionPlanService.countByProductId(product.getId()))
                .activeSubscriptionsCount(subscriptionService.countActiveByProductId(product.getId()))
                .build()
        );

        model.addAttribute("products", products);
        model.addAttribute("search", search);
        model.addAttribute("selectedOrganisation", organisationId);
        model.addAttribute("organisations", organisationService.findByOwnerId(userId));

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

        Page<SubscriptionPlan> plansPage = subscriptionPlanService.findByOrganiserId(userId, search, productId, pageable);

        Page<OrganiserSubscriptionPlanDto> plans = plansPage.map(plan ->
                OrganiserSubscriptionPlanDto.builder()
                        .id(plan.getId())
                        .name(plan.getName())
                        .description(plan.getDescription())
                        .active(plan.isActive())
                        .price(plan.getPrice())
                        .periodDays(plan.getPeriodDays())
                        .productId(plan.getProduct().getId())
                        .productName(plan.getProduct().getName())
                        .activeSubscriptionsCount(subscriptionService.countActiveByPlanId(plan.getId()))
                        .build()
        );

        model.addAttribute("plans", plans);
        model.addAttribute("search", search);
        model.addAttribute("selectedProduct", productId);
        model.addAttribute("products", productService.findByOrganiserId(userId));

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
        if (organisationId != null) model.addAttribute("organisationId", organisationId);
        model.addAttribute("organisations", organisationService.findByOwnerId(userId));
        return "organiser/products/form";
    }

    @GetMapping("/products/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        try {
            Product product = productService.findById(id);
            checkOrganisationAccess(product.getOrganisation().getId());

            ProductStatisticsDto statistics = null;
            try {
                statistics = statisticsService.getProductStatistics(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            OrganiserProductDto productDto = OrganiserProductDto.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .active(product.isActive())
                    .organisationId(product.getOrganisation().getId())
                    .organisationName(product.getOrganisation().getName())
                    .createdAt(product.getCreatedAt())
                    .subscriptionPlansCount(subscriptionPlanService.countByProductId(id))
                    .activeSubscriptionsCount(subscriptionService.countActiveByProductId(id))
                    .monthlyIncome(paymentService.getMonthlyRevenueByProductId(id).doubleValue())
                    .totalIncome(paymentService.getTotalRevenueByProductId(id).doubleValue())
                    .build();

            List<SubscriptionPlanDto> plans = subscriptionPlanService.findByProductId(id).stream()
                    .map(plan -> SubscriptionPlanDto.builder()
                            .id(plan.getId())
                            .name(plan.getName())
                            .description(plan.getDescription())
                            .price(plan.getPrice())
                            .periodDays(plan.getPeriodDays())
                            .active(plan.isActive())
                            .productId(plan.getProduct().getId())
                            .build())
                    .collect(Collectors.toList());

            model.addAttribute("product", productDto);
            if (statistics != null) {
                model.addAttribute("statistics", statistics);
            }
            model.addAttribute("subscriptionPlans", plans);
            
            return "organiser/products/view";
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
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
    public String createSubscriptionPlan(Model model) {
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email);
        Long userId = user.getId();
        
        model.addAttribute("plan", new SubscriptionPlanDto());
        model.addAttribute("products", productService.findByOrganiserId(userId));
        return "organiser/subscription-plans/form";
    }

    @GetMapping("/subscription-plans/{id}")
    public String viewSubscriptionPlan(@PathVariable Long id, Model model) {
        try {
            SubscriptionPlan plan = subscriptionPlanService.getPlan(id);
            checkOrganisationAccess(plan.getProduct().getOrganisation().getId());

            SubscriptionPlanDto planDto = mapper.toDto(plan);
            model.addAttribute("plan", planDto);

            try {
                SubscriptionPlanStatisticsDto statistics = statisticsService.getPlanStatistics(id);
                model.addAttribute("statistics", statistics);
            } catch (Exception e) {
                e.printStackTrace();
                // В случае ошибки добавляем пустую статистику
                model.addAttribute("statistics", new SubscriptionPlanStatisticsDto());
            }

            return "organiser/subscription-plans/view";
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
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
        Product product = productService.findById(productId);
        checkOrganisationAccess(product.getOrganisation().getId());

        SubscriptionPlanDto plan = new SubscriptionPlanDto();
        plan.setProductId(productId);

        ProductDto productDto = mapper.toDto(product);
        
        model.addAttribute("plan", plan);
        model.addAttribute("productId", productId);
        model.addAttribute("products", List.of(productDto));
        
        return "organiser/subscription-plans/form";
    }

    @PostMapping("/subscription-plans/{id}/toggle")
    @ResponseBody
    public ResponseEntity<?> togglePlanStatus(@PathVariable Long id) {
        try {
            SubscriptionPlan plan = subscriptionPlanService.togglePlanStatus(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "success", true,
                            "active", plan.isActive(),
                            "message", "Статус плана успешно изменен"
                    ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "success", false,
                            "message", "Ошибка при изменении статуса плана"
                    ));
        }
    }

    @PostMapping("/organisations/{id}/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleOrganisationStatus(@PathVariable Long id) {
        try {
            Organisation org = organisationService.toggleStatus(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "success", true,
                            "active", org.getActive(),
                            "message", "Статус организации успешно изменен"
                    ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "success", false,
                            "message", "Ошибка при изменении статуса организации"
                    ));
        }
    }

    @PostMapping("/products/{id}/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleProductStatus(@PathVariable Long id) {
        try {
            Product product = productService.toggleStatus(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "success", true,
                            "active", product.isActive(),
                            "message", "Статус продукта успешно изменен"
                    ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "success", false,
                            "message", "Ошибка при изменении статуса продукта"
                    ));
        }
    }

    @PostMapping("/subscriptions/{id}/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleSubscriptionStatus(@PathVariable Long id) {
        try {
            Subscription subscription = subscriptionService.toggleStatus(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "success", true,
                            "active", subscription.isActive(),
                            "message", "Статус подписки успешно изменен"
                    ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "success", false,
                            "message", "Ошибка при изменении статуса подписки"
                    ));
        }
    }

    @GetMapping("/subscription-plans/recommendation")
    @ResponseBody
    public ResponseEntity<?> getPlanRecommendation(@RequestParam Long productId) {
        try {
            log.info("Getting recommendation for product: {}", productId);
            SubscriptionPlanRecommendationDto recommendation = recommendationService.getRecommendation(productId);
            log.info("Recommendation: {}", recommendation);
            
            // Убедимся, что все числовые значения не null
            if (recommendation.getExpectedMonthlyRevenue() == null) {
                recommendation.setExpectedMonthlyRevenue(0.0);
            }
            if (recommendation.getConversionRate() == null) {
                recommendation.setConversionRate(0.0);
            }
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(recommendation);
        } catch (Exception e) {
            log.error("Error getting recommendation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                    "error", "Не удалось получить рекомендацию: " + e.getMessage(),
                    "status", "error"
                ));
        }
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