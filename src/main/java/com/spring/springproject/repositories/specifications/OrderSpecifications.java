package com.spring.springproject.repositories.specifications;

import com.spring.springproject.entities.Order;
import com.spring.springproject.entities.Status;
import com.spring.springproject.entities.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Date;

public class OrderSpecifications {

    public static Specification<Order> withFilters(User user, Double minAmount, Double maxAmount,
                                                   Status status, Date startDate, Date endDate) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true); // Ensure distinct results

            return criteriaBuilder.and(
                    user == null ? criteriaBuilder.isTrue(criteriaBuilder.literal(true)) :
                            criteriaBuilder.equal(root.get("user"), user),

                    minAmount == null ? criteriaBuilder.isTrue(criteriaBuilder.literal(true)) :
                            criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount),

                    maxAmount == null ? criteriaBuilder.isTrue(criteriaBuilder.literal(true)) :
                            criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount),

                    status == null ? criteriaBuilder.isTrue(criteriaBuilder.literal(true)) :
                            criteriaBuilder.equal(root.get("status"), status),

                    startDate == null ? criteriaBuilder.isTrue(criteriaBuilder.literal(true)) :
                            criteriaBuilder.greaterThanOrEqualTo(root.get("orderDate"), startDate),

                    endDate == null ? criteriaBuilder.isTrue(criteriaBuilder.literal(true)) :
                            criteriaBuilder.lessThanOrEqualTo(root.get("orderDate"), endDate)
            );
        };
    }
}
