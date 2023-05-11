package com.spring.springproject.specifications;

import com.spring.springproject.entities.Store;
import com.spring.springproject.entities.Store_;
import com.spring.springproject.entities.Type;
import com.spring.springproject.entities.Type_;
import org.springframework.data.jpa.domain.Specification;
import org.thymeleaf.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class StoreSpecification {
    public static Specification<Store> searchStore(String name, String address){
        return ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(!StringUtils.isEmptyOrWhitespace(name)){
                predicates.add(criteriaBuilder.like(root.get(Store_.NAME), "%" + name + "%"));
            }
            if(!StringUtils.isEmptyOrWhitespace(address)){
                predicates.add(criteriaBuilder.like(root.get(Store_.ADDRESS), "%" + address + "%"));
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        });
    }
}
