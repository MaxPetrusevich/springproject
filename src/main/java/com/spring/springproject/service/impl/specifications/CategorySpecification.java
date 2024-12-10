package com.spring.springproject.service.impl.specifications;

import com.spring.springproject.entities.Category;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {

    // Filter by category name
    public static Specification<Category> filterByCategoryName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name != null && !name.isEmpty()) {
                return criteriaBuilder.like(root.get("category"), "%" + name + "%");
            }
            return criteriaBuilder.conjunction(); // No condition if name is null or empty
        };
    }

    // Combine all filters into one
    public static Specification<Category> filterCategories(String name) {
        return Specification.where(filterByCategoryName(name));
    }
}
