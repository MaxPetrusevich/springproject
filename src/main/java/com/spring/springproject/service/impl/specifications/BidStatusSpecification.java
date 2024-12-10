package com.spring.springproject.service.impl.specifications;

import com.spring.springproject.entities.BidStatus;
import org.springframework.data.jpa.domain.Specification;

public class BidStatusSpecification {

    // Filter by status
    public static Specification<BidStatus> filterByStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status != null && !status.isEmpty()) {
                return criteriaBuilder.like(root.get("status"), "%" + status + "%");
            }
            return criteriaBuilder.conjunction(); // No condition if status is null or empty
        };
    }

    // Combine all filters into one
    public static Specification<BidStatus> filterBidStatuses(String status) {
        return Specification.where(filterByStatus(status));
    }
}
