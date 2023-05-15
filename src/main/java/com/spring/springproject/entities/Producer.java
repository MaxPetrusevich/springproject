package com.spring.springproject.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Producer implements Serializable {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producer_id")
    private Integer id;

    @EqualsAndHashCode.Exclude
    @Column(name = "producer_name")
    private String name;

    @EqualsAndHashCode.Exclude
    @Column(name = "producer_country")
    private String country;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "producer")
    private Set<Technique> techniques = new HashSet<Technique>();
}
