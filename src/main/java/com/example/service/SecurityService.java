package com.example.service;

import com.example.entity.Product;
import com.example.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {
    
    private final ProductService productService;
    private final SubscriptionService subscriptionService;
    private final OrganisationService organisationService;
    public boolean isProductOwner(Long productId, User user) {
        Product product = productService.getProduct(productId);
        return product.getOrganisation().getOwner().getId().equals(user.getId());
    }
    
    public boolean isSubscriptionOwner(Long subscriptionId, User user) {
        return subscriptionService.findById(subscriptionId)
            .getUser().getId().equals(user.getId());
    }
    
    public boolean isOrganisationOwner(Long organisationId, User user) {
        return organisationService.findById(organisationId)
            .getOwner().getId().equals(user.getId());
    }
} 