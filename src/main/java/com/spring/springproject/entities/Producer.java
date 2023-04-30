package com.spring.springproject.entities;

import javax.persistence.*;
import lombok.*;

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

    @Column(name = "producer_name")
    private String name;

    @Column(name = "producer_country")
    private String country;
    @ToString.Exclude
    @OneToMany(mappedBy = "producer")
    private Set<Technique> techniques = new HashSet<Technique>();
}
