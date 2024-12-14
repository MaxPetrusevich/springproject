package com.example.service;

import com.example.dto.SubscriptionPlanDto;
import com.example.entity.SubscriptionPlan;
import com.example.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<SubscriptionPlan> findAll() {
        return subscriptionPlanRepository.findAll();
    }

    @Transactional(readOnly = true)
    public SubscriptionPlan getPlan(Long id) {
        return subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription plan not found"));
    }

    @Transactional
    public SubscriptionPlan createPlan(SubscriptionPlanDto dto) {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setPrice(dto.getPrice());
        plan.setPeriodDays(dto.getPeriodDays());
        plan.setProduct(productService.findById(dto.getProductId()));
        plan.setActive(true);
        return subscriptionPlanRepository.save(plan);
    }

    @Transactional
    public SubscriptionPlan updatePlan(Long id, SubscriptionPlanDto dto) {
        SubscriptionPlan plan = getPlan(id);
        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setPrice(dto.getPrice());
        plan.setPeriodDays(dto.getPeriodDays());
        plan.setActive(dto.isActive());
        return subscriptionPlanRepository.save(plan);
    }

    @Transactional
    public void deletePlan(Long id) {
        subscriptionPlanRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionPlan> findByProductId(Long productId) {
        return subscriptionPlanRepository.findByProductId(productId);
    }

    @Transactional(readOnly = true)
    public long countActiveSubscriptions(Long planId) {
        return subscriptionPlanRepository.countActiveSubscriptions(planId);
    }

    @Transactional(readOnly = true)
    public long countTotalSubscriptions(Long planId) {
        return subscriptionPlanRepository.countTotalSubscriptions(planId);
    }

    @Transactional(readOnly = true)
    public double getAverageSubscriptionDays(Long planId) {
        return subscriptionPlanRepository.getAverageSubscriptionDays(planId);
    }

    @Transactional(readOnly = true)
    public double getRenewalRate(Long planId) {
        return subscriptionPlanRepository.getRenewalRate(planId);
    }

    @Transactional(readOnly = true)
    public long countActivePlansByProductId(Long productId) {
        return subscriptionPlanRepository.countActiveByProductId(productId);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionPlan> getActiveSubscriptions(Long planId) {
        return subscriptionPlanRepository.findByIdAndActive(planId, true);
    }

    @Transactional
    public void togglePlanStatus(Long id) {
        SubscriptionPlan plan = getPlan(id);
        plan.setActive(!plan.isActive());
        subscriptionPlanRepository.save(plan);
    }

    @Transactional(readOnly = true)
    public Page<SubscriptionPlan> findAllPlans(String search, Long productId, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            if (productId != null) {
                return subscriptionPlanRepository.findByNameContainingAndProductId(search, productId, pageable);
            }
            return subscriptionPlanRepository.findByNameContaining(search, pageable);
        } else if (productId != null) {
            return subscriptionPlanRepository.findByProductId(productId, pageable);
        }
        return subscriptionPlanRepository.findAll(pageable);
    }

    public Page<SubscriptionPlan> findByOwnerId(Long userId, String search, Long productId, Pageable pageable) {
        if (search != null && !search.isEmpty() && productId != null) {
            return subscriptionPlanRepository.findByNameContainingAndProductIdAndProductOrganisationOwnerId(
                search, productId, userId, pageable);
        } else if (search != null && !search.isEmpty()) {
            return subscriptionPlanRepository.findByNameContainingAndProductOrganisationOwnerId(search, userId, pageable);
        } else if (productId != null) {
            return subscriptionPlanRepository.findByProductIdAndProductOrganisationOwnerId(productId, userId, pageable);
        }
        return subscriptionPlanRepository.findByProductOrganisationOwnerId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public long countByOrganisationId(Long organisationId) {
        return subscriptionPlanRepository.countByProductOrganisationId(organisationId);
    }

    @Transactional(readOnly = true)
    public long countActiveByOrganisationId(Long organisationId) {
        return subscriptionPlanRepository.countByProductOrganisationIdAndActive(organisationId, true);
    }

    @Transactional(readOnly = true)
    public long countByProductId(Long productId) {
        return subscriptionPlanRepository.countByProductId(productId);
    }

    @Transactional(readOnly = true)
    public long countActiveByProductId(Long productId) {
        return subscriptionPlanRepository.countByProduct_IdAndActive(productId, true);
    }
}