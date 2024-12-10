package com.spring.springproject.service.impl;

import com.spring.springproject.entities.PaymentStatus;
import com.spring.springproject.repositories.PaymentStatusRepository;
import com.spring.springproject.service.impl.specifications.PaymentStatusSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PaymentStatusService {

    private final PaymentStatusRepository repository;

    // Find all payment statuses with pagination and optional status filtering
    public Page<PaymentStatus> findAll(Pageable pageable, String status) {
        Page<PaymentStatus> paymentStatuses = repository.findAll(
                PaymentStatusSpecification.filterByStatus(status), pageable);
        return new PageImpl<>(paymentStatuses.getContent(), pageable, paymentStatuses.getTotalElements());
    }

    // Find all payment statuses without pagination
    public Set<PaymentStatus> findAll() {
        return new HashSet<>(repository.findAll());
    }

    // Find a payment status by its ID
    public PaymentStatus findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    // Save a new payment status
    @Transactional
    public PaymentStatus save(PaymentStatus paymentStatus) {
        return repository.save(paymentStatus);
    }

    // Update an existing payment status
    @Transactional
    public void update(PaymentStatus paymentStatus) {
        repository.save(paymentStatus); // Save will handle both create and update operations
    }

    // Delete a payment status by its ID
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // Update the payment status' name
    @Transactional
    public void update(Long id, String status) {
        PaymentStatus paymentStatus = repository.findById(id).orElse(null);
        if (paymentStatus != null) {
            paymentStatus.setStatus(status);
            repository.save(paymentStatus); // Update the payment status name
        }
    }

    // Save a payment status with a given name
    @Transactional
    public PaymentStatus save(String status) {
        PaymentStatus paymentStatus = new PaymentStatus();
        paymentStatus.setStatus(status);
        return repository.save(paymentStatus);
    }
}
