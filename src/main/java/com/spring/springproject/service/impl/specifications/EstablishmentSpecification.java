package com.spring.springproject.service.impl.specifications;

import com.spring.springproject.entities.Establishment;
import org.springframework.data.jpa.domain.Specification;

public class EstablishmentSpecification {

    public static Specification<Establishment> filterByName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name != null && !name.isEmpty()) {
                return criteriaBuilder.like(root.get("name"), "%" + name + "%");
            }
            return criteriaBuilder.conjunction(); // No condition if name is null or empty
        };
    }
}
