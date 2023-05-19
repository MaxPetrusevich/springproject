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
public class Store implements Serializable {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Integer id;

    @EqualsAndHashCode.Exclude
    @Column(name = "store_name")
    private String name;

    @EqualsAndHashCode.Exclude
    @Column(name = "store_address")
    private String address;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "storeList")
    private Set<Technique> techniques = new HashSet<Technique>();
}
