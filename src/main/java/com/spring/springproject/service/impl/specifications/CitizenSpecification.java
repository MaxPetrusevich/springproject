package com.spring.springproject.service.impl.specifications;

import com.spring.springproject.entities.Citizen;
import org.springframework.data.jpa.domain.Specification;

public class CitizenSpecification {

    // Filter by first name
    public static Specification<Citizen> filterByFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> {
            if (firstName != null && !firstName.isEmpty()) {
                return criteriaBuilder.like(root.get("firstName"), "%" + firstName + "%");
            }
            return criteriaBuilder.conjunction(); // No condition if firstName is null or empty
        };
    }

    // Filter by last name
    public static Specification<Citizen> filterByLastName(String lastName) {
        return (root, query, criteriaBuilder) -> {
            if (lastName != null && !lastName.isEmpty()) {
                return criteriaBuilder.like(root.get("lastName"), "%" + lastName + "%");
            }
            return criteriaBuilder.conjunction(); // No condition if lastName is null or empty
        };
    }

    // Filter by passport number
    public static Specification<Citizen> filterByPassportNumber(String passportNumber) {
        return (root, query, criteriaBuilder) -> {
            if (passportNumber != null && !passportNumber.isEmpty()) {
                return criteriaBuilder.like(root.get("passportNumber"), "%" + passportNumber + "%");
            }
            return criteriaBuilder.conjunction(); // No condition if passportNumber is null or empty
        };
    }

    // Filter by phone number
    public static Specification<Citizen> filterByPhone(String phone) {
        return (root, query, criteriaBuilder) -> {
            if (phone != null && !phone.isEmpty()) {
                return criteriaBuilder.like(root.get("phone"), "%" + phone + "%");
            }
            return criteriaBuilder.conjunction(); // No condition if phone is null or empty
        };
    }

    // Filter by identify number
    public static Specification<Citizen> filterByIdentifyNumber(String identifyNumber) {
        return (root, query, criteriaBuilder) -> {
            if (identifyNumber != null && !identifyNumber.isEmpty()) {
                return criteriaBuilder.like(root.get("identifyNumber"), "%" + identifyNumber + "%");
            }
            return criteriaBuilder.conjunction(); // No condition if identifyNumber is null or empty
        };
    }

    // Combine all the filters into one
    public static Specification<Citizen> filterCitizens(String firstName, String lastName, String passportNumber, String phone, String identifyNumber) {
        return Specification.where(filterByFirstName(firstName))
                .and(filterByLastName(lastName))
                .and(filterByPassportNumber(passportNumber))
                .and(filterByPhone(phone))
                .and(filterByIdentifyNumber(identifyNumber));
    }
}
