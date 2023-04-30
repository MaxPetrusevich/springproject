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
public class Store implements Serializable {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Integer id;

    @Column(name = "store_name")
    private String name;

    @Column(name = "store_address")
    private String address;
    @ToString.Exclude
    @ManyToMany(mappedBy = "storeList")
    private Set<Technique> techniques = new HashSet<Technique>();
}
