package com.spring.springproject.specifications;

import com.spring.springproject.entities.Category;
import com.spring.springproject.entities.Category_;
import org.springframework.data.jpa.domain.Specification;
import org.thymeleaf.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class CategorySpecification {
    public static Specification<Category> searchCategory(String name) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmptyOrWhitespace(name)) {
                predicates.add(criteriaBuilder.like(root.get(Category_.NAME), "%" + name + "%"));
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        });
    }
}
