package com.spring.springproject.specifications;

import com.spring.springproject.entities.Type;
import com.spring.springproject.entities.Type_;
import org.hibernate.criterion.Order;
import org.springframework.data.jpa.domain.Specification;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

public class TypeSpecification {
    public static Specification<Type> searchType(String name){
        return ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(!StringUtils.isEmptyOrWhitespace(name)){
                predicates.add(criteriaBuilder.like(root.get(Type_.NAME), "%" + name + "%"));
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        });
    }
}
