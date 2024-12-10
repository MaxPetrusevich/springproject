package com.spring.springproject.service.impl.specifications;

import com.spring.springproject.entities.Bid;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public class BidSpecification {

    // Filter by citizen ID
    public static Specification<Bid> filterByCitizenId(Long citizenId) {
        return (root, query, criteriaBuilder) -> {
            if (citizenId != null) {
                return criteriaBuilder.equal(root.get("citizen").get("id"), citizenId);
            }
            return criteriaBuilder.conjunction(); // No condition if citizenId is null
        };
    }

    // Filter by service ID
    public static Specification<Bid> filterByServiceId(Long serviceId) {
        return (root, query, criteriaBuilder) -> {
            if (serviceId != null) {
                return criteriaBuilder.equal(root.get("service").get("id"), serviceId);
            }
            return criteriaBuilder.conjunction();
        };
    }

    // Filter by status ID
    public static Specification<Bid> filterByStatusId(Long statusId) {
        return (root, query, criteriaBuilder) -> {
            if (statusId != null) {
                return criteriaBuilder.equal(root.get("status").get("id"), statusId);
            }
            return criteriaBuilder.conjunction();
        };
    }

    // Filter by date
    public static Specification<Bid> filterByDate(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            if (date != null) {
                return criteriaBuilder.equal(root.get("date"), date);
            }
            return criteriaBuilder.conjunction();
        };
    }

    // Combine all filters into one
    public static Specification<Bid> filterBids(Long citizenId, Long serviceId, Long statusId, LocalDate date) {
        return Specification
                .where(filterByCitizenId(citizenId))
                .and(filterByServiceId(serviceId))
                .and(filterByStatusId(statusId))
                .and(filterByDate(date));
    }
}
