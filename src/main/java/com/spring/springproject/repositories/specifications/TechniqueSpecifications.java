package com.spring.springproject.repositories.specifications;
import com.spring.springproject.entities.Technique;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class TechniqueSpecifications {

    public static Specification<Technique> filterByCriteria(
            Double startPrice, Double endPrice,
            String producerName, String modelName, String categoryName) {
        return (Root<Technique> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (startPrice != null && endPrice != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.between(root.get("price"), startPrice, endPrice)
                );
            }

            if (StringUtils.hasText(producerName)) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("producer").get("name")),
                                "%" + producerName.toLowerCase() + "%")
                );
            }

            if (StringUtils.hasText(modelName)) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("model").get("name")),
                                "%" + modelName.toLowerCase() + "%")
                );
            }

            if (StringUtils.hasText(categoryName)) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("category").get("name")),
                                "%" + categoryName.toLowerCase() + "%")
                );
            }

            return predicate;
        };
    }
}

