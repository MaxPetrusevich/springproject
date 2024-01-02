package com.spring.springproject.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer id;

    @Column(name = "order_amount")
    private Double amount;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_date")
    private Date orderDate;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderTechnique> orderTechniques = new HashSet<>();

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;
}