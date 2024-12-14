package com.example.controller.view;

import com.example.dto.ProductDetailsDto;
import com.example.dto.ProductDto;
import com.example.entity.Product;
import com.example.entity.SubscriptionPlan;
import com.example.mapper.EntityMapper;
import com.example.service.OrganisationService;
import com.example.service.ProductService;
import com.example.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/products")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final OrganisationService organisationService;
    private final StatisticsService statisticsService;
    private final EntityMapper mapper;
    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Long organisationId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        model.addAttribute("products", productService.findAll(search, category, active, organisationId, pageable));
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("organisations", organisationService.findAll());
        model.addAttribute("search", search);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("active", active);
        model.addAttribute("selectedOrganisation", organisationId);
        return "admin/products/list";
    }

    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        ProductDetailsDto dto = new ProductDetailsDto();
        
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setActive(product.isActive());
        dto.setCreatedAt(product.getCreatedAt());
        
        dto.setOrganisationId(product.getOrganisation().getId());
        dto.setOrganisationName(product.getOrganisation().getName());
        
        List<SubscriptionPlan> plans = productService.getSubscriptionPlans(id);
        dto.setPlans(plans.stream().map(mapper::toDto).collect(Collectors.toList()));
        dto.setTotalPlans(plans.size());
        dto.setActivePlans((int) plans.stream().filter(SubscriptionPlan::isActive).count());
        
        model.addAttribute("product", dto);
        model.addAttribute("statistics", statisticsService.getProductStatistics(id));
        return "admin/products/view";
    }

    @GetMapping("/create")
    public String createProductForm(
            @RequestParam(required = false) Long organisationId,
            Model model
    ) {
        ProductDto product = new ProductDto();
        if (organisationId != null) {
            product.setOrganisationId(organisationId);
        }
        model.addAttribute("product", product);
        model.addAttribute("organisations", organisationService.findAll());
        model.addAttribute("categories", productService.getAllCategories());
        return "admin/products/form";
    }

    @GetMapping("/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        model.addAttribute("organisations", organisationService.findAll());
        model.addAttribute("categories", productService.getAllCategories());
        return "admin/products/form";
    }

    @PostMapping
    public String createProduct(@ModelAttribute ProductDto productDto) {
        Product product = productService.create(productDto);
        return "redirect:/admin/products/" + product.getId();
    }

    @PostMapping("/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute ProductDto productDto
    ) {
        productService.update(id, productDto);
        return "redirect:/admin/products/" + id;
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleProductStatus(@PathVariable Long id) {
        productService.toggleStatus(id);
        return "redirect:/admin/products/" + id;
    }
} 