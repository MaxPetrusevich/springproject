package com.spring.springproject.service.impl.specifications;

import com.spring.springproject.entities.PaymentStatus;
import org.springframework.data.jpa.domain.Specification;

public class PaymentStatusSpecification {

    public static Specification<PaymentStatus> filterByStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status != null && !status.isEmpty()) {
                return criteriaBuilder.like(root.get("status"), "%" + status + "%");
            }
            return criteriaBuilder.conjunction(); // No condition if status is null or empty
        };
    }
}
