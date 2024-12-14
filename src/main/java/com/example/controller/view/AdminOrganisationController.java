package com.example.controller.view;

import com.example.dto.*;
import com.example.entity.Organisation;
import com.example.entity.Product;
import com.example.enums.UserRole;
import com.example.mapper.EntityMapper;
import com.example.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/organisations")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminOrganisationController {

    private final OrganisationService organisationService;
    private final ProductService productService;
    private final UserService userService;
    private final EntityMapper entityMapper;


    @GetMapping
    public String list(Model model) {
        List<OrganisationDto> organisations = organisationService.findAll().stream()
            .map(org -> {
                OrganisationDto dto = new OrganisationDto();
                dto.setId(org.getId());
                dto.setName(org.getName());
                dto.setDescription(org.getDescription());
                dto.setActive(org.getActive());
                dto.setOwnerId(org.getOwner().getId());
                dto.setOwnerEmail(org.getOwner().getEmail());
                dto.setSubscribersCount(organisationService.countSubscribers(org.getId()));
                return dto;
            })
            .collect(Collectors.toList());
            
        model.addAttribute("organisations", organisations);
        return "admin/organisations/list";
    }

    @GetMapping("/{id}")
    public String viewOrganisation(@PathVariable Long id, Model model) {
        Organisation org = organisationService.findById(id);
        AdminOrganisationDetailsDto dto = new AdminOrganisationDetailsDto();
        
        dto.setId(org.getId());
        dto.setName(org.getName());
        dto.setDescription(org.getDescription());
        dto.setOwnerId(org.getOwner().getId());
        dto.setOwnerName(org.getOwner().getLastName() + " " + org.getOwner().getFirstName());
        dto.setCreatedAt(org.getCreatedAt());
        dto.setActive(org.getActive());
        
        List<Product> products = productService.findByOrganisationId(id);
        dto.setProducts(products.stream()
            .map(p -> {
                AdminProductDto productDto = new AdminProductDto();
                productDto.setId(p.getId());
                productDto.setName(p.getName());
                productDto.setDescription(p.getDescription());
                productDto.setActive(p.isActive());
                return productDto;
            })
            .collect(Collectors.toList()));
        
        dto.setProductsCount(products.size());
        dto.setActiveProductsCount((int) products.stream().filter(Product::isActive).count());
        
        model.addAttribute("organisation", dto);
        return "admin/organisations/view";
    }

    @GetMapping("/create")
    public String createOrganisationForm(Model model) {
        model.addAttribute("organisation", new OrganisationDto());
        model.addAttribute("users", userService.findByRole(UserRole.ORGANIZER));
        return "admin/organisations/form";
    }

    @GetMapping("/{id}/edit")
    public String editOrganisationForm(@PathVariable Long id, Model model) {
        model.addAttribute("organisation", entityMapper.toOrganisationDto(organisationService.findById(id)));
        model.addAttribute("users", userService.findByRole(UserRole.ORGANIZER));
        return "admin/organisations/form";
    }

    @PostMapping
    public String createOrganisation(@ModelAttribute OrganisationDto organisationDto) {
        organisationService.create(organisationDto);
        return "redirect:/admin/organisations";
    }

    @PostMapping("/{id}")
    public String updateOrganisation(
            @PathVariable Long id,
            @ModelAttribute OrganisationDto organisationDto
    ) {
        organisationService.update(id, organisationDto);
        return "redirect:/admin/organisations/" + id;
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleOrganisationStatus(@PathVariable Long id) {
        organisationService.toggleStatus(id);
        return "redirect:/admin/organisations/" + id;
    }
} 