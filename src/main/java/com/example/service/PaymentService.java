package com.example.service;

import com.example.entity.Payment;
import com.example.entity.Subscription;
import com.example.enums.PaymentStatus;
import com.example.mapper.EntityMapper;
import com.example.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final EntityMapper mapper;

    @Transactional(readOnly = true)
    public List<Payment> getAllUserPayments(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Payment> getRecentPayments(Long userId) {
        return paymentRepository.findTop5ByUserIdOrderByPaymentDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    @Transactional
    public Payment createPayment(Subscription subscription) {
        Payment payment = new Payment();
        payment.setUser(subscription.getUser());
        payment.setSubscription(subscription);
        payment.setAmount(subscription.getPlan().getPrice());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PENDING);
        
        payment = paymentRepository.save(payment);
        
        // Отправляем уведомление
        emailService.sendPaymentConfirmation(payment.getUser().getEmail(), payment);
        
        return payment;
    }

    @Transactional
    public Payment completePayment(Long id) {
        Payment payment = findById(id);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment = paymentRepository.save(payment);
        
        // Отправляем уведомление
        emailService.sendPaymentSuccess(payment.getUser().getEmail(), payment);
        
        return payment;
    }

    @Transactional
    public Payment failPayment(Long id, String reason) {
        Payment payment = findById(id);
        payment.setStatus(PaymentStatus.FAILED);
        payment = paymentRepository.save(payment);
        
        // Отправляем уведомление
        emailService.sendPaymentFailure(payment.getUser().getEmail(), payment, reason);
        
        return payment;
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateTotalRevenue() {
        return paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateMonthlyRevenue() {
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        return paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .filter(p -> p.getPaymentDate().isAfter(monthAgo))
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public List<Payment> findByUserId(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Payment getPaymentById(Long id) {
        return findById(id);
    }

    public BigDecimal getTotalRevenueByOrganisationId(Long organisationId) {
        return paymentRepository.sumByOrganisationId(organisationId);
    }

    public BigDecimal getMonthlyRevenueByOrganisationId(Long organisationId) {
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        return paymentRepository.sumByOrganisationIdAndCreatedAtAfter(organisationId, monthAgo);
    }

    public List<Payment> findAllBySubscriptionId(Long subscriptionId) {
        return paymentRepository.findBySubscriptionIdOrderByCreatedAtDesc(subscriptionId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getMonthlyRevenueByProductId(Long productId) {
        return paymentRepository.getMonthlyRevenueByProductId(productId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenueByProductId(Long productId) {
        return paymentRepository.getTotalRevenueByProductId(productId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getMonthlyRevenueByPlanId(Long planId) {
        return paymentRepository.getMonthlyRevenueByPlanId(planId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenueByPlanId(Long planId) {
        return paymentRepository.getTotalRevenueByPlanId(planId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenueByOrganiserId(Long userId) {
        return paymentRepository.sumByOrganisationOwnerId(userId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getMonthlyRevenueByOrganiserId(Long userId) {
        LocalDateTime startOfMonth = LocalDateTime.now()
            .withDayOfMonth(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);
        return paymentRepository.sumByOrganisationOwnerIdAndCreatedAtAfter(userId, startOfMonth);
    }
} 