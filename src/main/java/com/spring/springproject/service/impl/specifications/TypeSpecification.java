package com.spring.springproject.service.impl.specifications;

import com.spring.springproject.entities.Type;
import org.springframework.data.jpa.domain.Specification;

public class TypeSpecification {

    public static Specification<Type> filterByTypeName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name != null && !name.isEmpty()) {
                return criteriaBuilder.like(root.get("type"), "%" + name + "%");
            }
            return criteriaBuilder.conjunction(); // No condition if name is null or empty
        };
    }
}
