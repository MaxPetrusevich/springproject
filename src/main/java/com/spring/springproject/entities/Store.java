package com.spring.springproject.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
@ToString(exclude = {"techniques"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Store implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Integer id;

    @Column(name = "store_name")
    private String name;

    @Column(name = "store_address")
    private String address;

    @ManyToMany(mappedBy = "storeList")
    private Set<Technique> techniques = new HashSet<Technique>();
}
