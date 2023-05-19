package com.spring.springproject.specifications;

import com.spring.springproject.entities.Producer;
import com.spring.springproject.entities.Producer_;
import org.springframework.data.jpa.domain.Specification;
import org.thymeleaf.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ProducerSpecification {
    public static Specification<Producer> searchProducer(String name, String country) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmptyOrWhitespace(name)) {
                predicates.add(criteriaBuilder.like(root.get(Producer_.NAME), "%" + name + "%"));
            }
            if (!StringUtils.isEmptyOrWhitespace(country)) {
                predicates.add(criteriaBuilder.like(root.get(Producer_.COUNTRY), "%" + country + "%"));
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        });
    }
}
