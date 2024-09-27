package com.spring.springproject.dto;

import com.spring.springproject.entities.Status;
import com.spring.springproject.entities.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    private Integer id;
    @EqualsAndHashCode.Exclude
    private Double amount;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserDto user;
    @EqualsAndHashCode.Exclude
    private Date orderDate;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<OrderTechniqueDto> orderTechniques = new HashSet<>();
    @EqualsAndHashCode.Exclude
    private Status status;
}
