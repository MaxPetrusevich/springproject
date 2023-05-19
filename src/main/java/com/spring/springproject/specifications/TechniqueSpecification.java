package com.spring.springproject.specifications;

import com.spring.springproject.entities.Technique;
import com.spring.springproject.entities.Technique_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class TechniqueSpecification {
    public static Specification<Technique> searchTechnique(Double startPrice, Double endPrice) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (startPrice != null && endPrice != null) {
                predicates.add(criteriaBuilder.le(root.get(Technique_.PRICE), endPrice));
                predicates.add(criteriaBuilder.and(criteriaBuilder.ge(root.get(Technique_.PRICE), startPrice)));
            } else if (endPrice != null) {
                predicates.add(criteriaBuilder.le(root.get(Technique_.PRICE), endPrice));
            } else if (startPrice != null) {
                predicates.add(criteriaBuilder.ge(root.get(Technique_.PRICE), startPrice));
            }


            // predicates.add((Predicate) criteriaBuilder.asc((Expression<?>) Order.asc(Type_.ID)));
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        });
    }
}
