package com.spring.springproject.repositories.specifications;

import com.spring.springproject.entities.User;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class UserSpecifications {

    public static Specification<User> filterByUser(User user) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (user.getUserName() != null && !user.getUserName().isEmpty()) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("userName")),
                                "%" + user.getUserName().toLowerCase() + "%"
                        )
                );
            }
            if (user.getName() != null && !user.getName().isEmpty()) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                "%" + user.getName().toLowerCase() + "%"
                        )
                );
            }

            if (user.getSurname() != null && !user.getSurname().isEmpty()) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("surname")),
                                "%" + user.getSurname().toLowerCase() + "%"
                        )
                );
            }

            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("email")),
                                "%" + user.getEmail().toLowerCase() + "%"
                        )
                );
            }

            return predicate;
        };
    }
}
