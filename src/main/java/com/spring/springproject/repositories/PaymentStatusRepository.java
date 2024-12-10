package com.spring.springproject.repositories;

import com.spring.springproject.entities.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface  PaymentStatusRepository extends JpaRepository<PaymentStatus, Long>, JpaSpecificationExecutor<PaymentStatus> {
}
