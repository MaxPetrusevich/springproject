package com.spring.springproject.service.impl.specifications;

import com.spring.springproject.entities.Document;
import org.springframework.data.jpa.domain.Specification;

public class DocumentSpecification {

    public static Specification<Document> filterByLink(String link) {
        return (root, query, criteriaBuilder) -> {
            if (link != null && !link.isEmpty()) {
                return criteriaBuilder.like(root.get("link"), "%" + link + "%");
            }
            return criteriaBuilder.conjunction(); // No condition if link is null or empty
        };
    }
}
