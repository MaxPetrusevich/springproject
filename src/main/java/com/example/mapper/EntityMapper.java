package com.example.mapper;

import com.example.dto.*;
import com.example.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        dto.setEnabled(user.isEnabled());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    public ProductDto toDto(Product product) {
        if (product == null) return null;
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setCategory(product.getCategory());
        dto.setActive(product.isActive());
        dto.setOrganisationId(product.getOrganisation() != null ? product.getOrganisation().getId() : null);
        return dto;
    }

    public SubscriptionPlanDto toDto(SubscriptionPlan plan) {
        if (plan == null) return null;
        SubscriptionPlanDto dto = new SubscriptionPlanDto();
        dto.setId(plan.getId());
        dto.setName(plan.getName());
        dto.setDescription(plan.getDescription());
        dto.setPrice(plan.getPrice());
        dto.setPeriodDays(plan.getPeriodDays());
        dto.setProductId(plan.getProduct().getId());
        dto.setProductName(plan.getProduct().getName());
        dto.setActive(plan.isActive());
        return dto;
    }

    public List<UserDto> toUserDtos(List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ProductDto> toProductDtos(List<Product> products) {
        return products.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<SubscriptionPlanDto> toPlanDtos(List<SubscriptionPlan> plans) {
        return plans.stream().map(this::toDto).collect(Collectors.toList());
    }

    public SubscriptionDto toSubscriptionDto(Subscription subscription) {
        if (subscription == null) return null;

        SubscriptionDto dto = new SubscriptionDto();
        dto.setId(subscription.getId());
        dto.setProductName(subscription.getPlan().getProduct().getName());
        dto.setPlanName(subscription.getPlan().getName());
        dto.setStatus(subscription.getStatus());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setActive(subscription.isActive());
        dto.setPrice(subscription.getPlan().getPrice());
        return dto;
    }

    public PaymentDto toPaymentDto(Payment payment) {
        if (payment == null) return null;
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setUserId(payment.getUser().getId());
        dto.setSubscriptionId(payment.getSubscription().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setStatus(payment.getStatus());
        dto.setSubscription(toSubscriptionDto(payment.getSubscription()));
        return dto;
    }

    public List<SubscriptionDto> toSubscriptionDtos(List<Subscription> subscriptions) {
        if (subscriptions == null) return null;
        return subscriptions.stream()
                .map(this::toSubscriptionDto)
                .collect(Collectors.toList());
    }

    public List<PaymentDto> toPaymentDtos(List<Payment> payments) {
        return payments.stream().map(this::toPaymentDto).collect(Collectors.toList());
    }

    public Page<SubscriptionDto> toSubscriptionDtos(Page<Subscription> page) {
        if (page == null) return null;
        return page.map(this::toSubscriptionDto);
    }

    public OrganisationDto toOrganisationDto(Organisation organisation) {
        if (organisation == null) return null;
        OrganisationDto dto = new OrganisationDto();
        dto.setId(organisation.getId());
        dto.setName(organisation.getName());
        dto.setDescription(organisation.getDescription());
        dto.setOwnerId(organisation.getOwner().getId());
        return dto;
    }

    public List<OrganisationDto> toOrganisationDtos(List<Organisation> organisations) {
        if (organisations == null) return null;
        return organisations.stream()
                .map(this::toOrganisationDto)
                .collect(Collectors.toList());
    }

    public Page<OrganisationDto> toOrganisationDtos(Page<Organisation> page) {
        if (page == null) return null;
        return page.map(this::toOrganisationDto);
    }

    public Page<UserDto> toUserDtoPage(Page<User> users) {
        return users.map(this::toDto);
    }

    public CustomerSubscriptionDto toCustomerSubscriptionDto(Subscription subscription) {
        if (subscription == null) return null;

        return CustomerSubscriptionDto.builder()
                .id(subscription.getId())
                .status(subscription.getStatus())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .price(subscription.getPlan().getPrice())
                .build();
    }

    public CustomerProductDto toCustomerProductDto(Product product) {
        return CustomerProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .organisationName(product.getOrganisation().getName())
                .active(product.isActive())
                .organisationId(product.getOrganisation().getId())
                .plans(toPlanDtos(product.getSubscriptionPlans()))
                .category(product.getCategory())
                .availablePlansCount(product.getSubscriptionPlans().stream().filter(SubscriptionPlan::isActive).count())
                .build();
    }

    public List<CustomerProductDto> toCustomerProductDtos(List<Product> products) {
        return products.stream()
                .map(this::toCustomerProductDto)
                .collect(Collectors.toList());
    }

    private BigDecimal getMinPrice(List<SubscriptionPlan> plans) {
        return plans.stream()
                .map(SubscriptionPlan::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal getMaxPrice(List<SubscriptionPlan> plans) {
        return plans.stream()
                .map(SubscriptionPlan::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    public Product toEntity(ProductDto dto) {
        if (dto == null) return null;
        
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCategory(dto.getCategory());
        product.setActive(dto.isActive());
        
        if (dto.getOrganisationId() != null) {
            Organisation organisation = new Organisation();
            organisation.setId(dto.getOrganisationId());
            product.setOrganisation(organisation);
        }
        
        return product;
    }

} 