package com.example.service;

import com.example.dto.SubscriptionDto;
import com.example.dto.CustomerSubscriptionDto;
import com.example.entity.Subscription;
import com.example.entity.SubscriptionPlan;
import com.example.entity.User;
import com.example.entity.Payment;
import com.example.enums.SubscriptionStatus;
import com.example.mapper.EntityMapper;
import com.example.repository.SubscriptionRepository;
import com.example.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final SubscriptionPlanService subscriptionPlanService;
    private final UserService userService;
    private final EmailService emailService;
    private final EntityMapper entityMapper;
    @Transactional(readOnly = true)
    public Page<Subscription> findAll(String search, SubscriptionStatus status, Long userId, Long planId, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            if (status != null) {
                return subscriptionRepository.findBySearchAndStatus(search, status, pageable);
            }
            return subscriptionRepository.findBySearch(search, pageable);
        }
        if (status != null) {
            return subscriptionRepository.findByStatus(status, pageable);
        }
        if (userId != null) {
            return subscriptionRepository.findByUserId(userId, pageable);
        }
        if (planId != null) {
            return subscriptionRepository.findByPlanId(planId, pageable);
        }
        return subscriptionRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Subscription findById(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
    }

    @Transactional(readOnly = true)
    public List<Payment> getPayments(Long subscriptionId) {
        return paymentRepository.findBySubscriptionId(subscriptionId);
    }

    @Transactional
    public Subscription subscribe(Long userId, Long planId) {
        User user = userService.findById(userId);
        SubscriptionPlan plan = subscriptionPlanService.getPlan(planId);
        
        // Проверяем, нет ли уже активной подписки
        if (subscriptionRepository.existsByUserAndPlanAndActive(user, plan, true)) {
            throw new RuntimeException("Active subscription already exists");
        }

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusDays(plan.getPeriodDays()));
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setActive(true);
        
        subscription = subscriptionRepository.save(subscription);
        
        // Отправляем уведомление
        emailService.sendSubscriptionConfirmation(user.getEmail(), subscription);
        
        return subscription;
    }

    @Transactional
    public Subscription cancelSubscription(Long id) {
        Subscription subscription = findById(id);
        subscription.setActive(false);
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setCancelledAt(LocalDateTime.now());
        
        subscription = subscriptionRepository.save(subscription);
        
        // Отправляем уведомление
        emailService.sendSubscriptionCancellation(subscription.getUser().getEmail(), subscription);
        
        return subscription;
    }

    @Transactional
    public Subscription renewSubscription(Long id) {
        Subscription oldSubscription = findById(id);
        SubscriptionPlan plan = oldSubscription.getPlan();
        
        Subscription subscription = new Subscription();
        subscription.setUser(oldSubscription.getUser());
        subscription.setPlan(plan);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusDays(plan.getPeriodDays()));
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setActive(true);
        subscription.setRenewedAt(LocalDateTime.now());
        
        subscription = subscriptionRepository.save(subscription);
        
        // Отправляем уведомление
        emailService.sendSubscriptionRenewal(subscription.getUser().getEmail(), subscription);
        
        return subscription;
    }

    @Transactional(readOnly = true)
    public List<Subscription> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Page<Subscription> findByUserId(Long userId) {
        return subscriptionRepository.findByUserId(userId, Pageable.unpaged());
    }

    @Transactional(readOnly = true)
    public List<Subscription> getActiveSubscriptions() {
        return subscriptionRepository.findByActive(true);
    }

    @Transactional(readOnly = true)
    public long countActiveSubscriptions() {
        return subscriptionRepository.countByActive(true);
    }

    @Transactional(readOnly = true)
    public List<Subscription> findTop5ByOrderByCreatedAtDesc() {
        return subscriptionRepository.findTop5ByOrderByCreatedAtDesc();
    }

    public long countByProductId(Long productId) {
        return subscriptionRepository.countByProductId(productId);
    }

    public long countByOrganisationId(Long organisationId) {
        return subscriptionRepository.countByOrganisationId(organisationId);
    }

    public long countActiveByOrganisationId(Long organisationId) {
        return subscriptionRepository.countByOrganisationIdAndActive(organisationId);
    }

    @Transactional(readOnly = true)
    public long countActiveByProductId(Long productId) {
        return subscriptionRepository.countByProductIdAndActive(productId, true);
    }

    @Transactional(readOnly = true)
    public long countActiveByPlanId(Long planId) {
        return subscriptionRepository.countByPlanIdAndActive(planId, true);
    }

    @Transactional(readOnly = true)
    public long countByPlanId(Long planId) {
        return subscriptionRepository.countByPlanId(planId);
    }

    @Transactional(readOnly = true)
    public double getRenewalRate(Long planId) {
        long totalSubscriptions = countByPlanId(planId);
        if (totalSubscriptions == 0) {
            return 0.0;
        }
        long renewedSubscriptions = subscriptionRepository.countRenewalsByPlanId(planId);
        return (double) renewedSubscriptions / totalSubscriptions * 100;
    }

    @Transactional(readOnly = true)
    public int countNewSubscriptionsThisMonth(Long planId) {
        LocalDateTime startOfMonth = LocalDateTime.now()
            .withDayOfMonth(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);
        return (int)subscriptionRepository.countByPlanIdAndCreatedAtAfter(planId, startOfMonth);
    }

    @Transactional(readOnly = true)
    public long countActiveByOrganisationOwnerId(Long ownerId) {
        return subscriptionRepository.countActiveByOrganisationOwnerId(ownerId);
    }

    @Transactional(readOnly = true)
    public double getRenewalRateByOrganisationOwnerId(Long ownerId) {
        long totalSubscriptions = subscriptionRepository.countByOrganisationOwnerId(ownerId);
        if (totalSubscriptions == 0) {
            return 0.0;
        }
        long renewedSubscriptions = subscriptionRepository.countRenewalsByOrganisationOwnerId(ownerId);
        return (double) renewedSubscriptions / totalSubscriptions * 100;
    }

    @Transactional(readOnly = true)
    public Page<CustomerSubscriptionDto> findByUserId(Long userId, String status, String sort, Pageable pageable) {
        Page<Subscription> subscriptions;
        
        if (status != null && !status.isEmpty()) {
            SubscriptionStatus subscriptionStatus = SubscriptionStatus.valueOf(status);
            subscriptions = subscriptionRepository.findByUserIdAndStatus(userId, subscriptionStatus, pageable);
        } else {
            subscriptions = subscriptionRepository.findByUserId(userId, pageable);
        }
        
        if (sort != null) {
            switch (sort) {
                case "createdAt":
                    pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                        Sort.by("createdAt").descending());
                    break;
                case "endDate":
                    pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                        Sort.by("endDate").ascending());
                    break;
                case "price":
                    pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                        Sort.by("plan.price").descending());
                    break;
            }
        }
        
        return subscriptions.map(subscription -> {
            CustomerSubscriptionDto dto = entityMapper.toCustomerSubscriptionDto(subscription);
            dto.setProductName(subscription.getPlan().getProduct().getName());
            dto.setPlanName(subscription.getPlan().getName());
            dto.setCanCancel(subscription.getStatus() == SubscriptionStatus.ACTIVE);
            dto.setStatusBadgeClass(getStatusBadgeClass(subscription.getStatus()));
            dto.setStatusText(getStatusText(subscription.getStatus()));
            return dto;
        });
    }

    private String getStatusBadgeClass(SubscriptionStatus status) {
        return switch (status) {
            case ACTIVE -> "badge-success";
            case EXPIRED -> "badge-primary";
            case CANCELLED -> "badge-danger";
            default -> "badge-secondary";
        };
    }

    private String getStatusText(SubscriptionStatus status) {
        return switch (status) {
            case ACTIVE -> "Активна";
            case EXPIRED -> "Завершена";
            case CANCELLED -> "Отменена";
            default -> status.name();
        };
    }

    public List<SubscriptionDto> findByUserEmail(String email) {
        return subscriptionRepository.findByUserEmail(email).stream()
            .map(entityMapper::toSubscriptionDto)
            .collect(Collectors.toList());
    }

    public List<SubscriptionDto> findByUserEmailAndStatus(String email, SubscriptionStatus status) {
        return subscriptionRepository.findByUserEmailAndStatus(email, status).stream()
            .map(entityMapper::toSubscriptionDto)
            .collect(Collectors.toList());
    }
} 