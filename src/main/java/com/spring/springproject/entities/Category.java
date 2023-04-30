package com.spring.springproject.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table
public class Category implements Serializable {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id;

    @Column(name = "category_name")
    private String name;
    @ToString.Exclude
    @OneToMany(mappedBy = "category")
    private Set<Type> types = new HashSet<Type>();
    @ToString.Exclude
    @OneToMany(mappedBy = "category")
    private Set<Technique> techniques = new HashSet<Technique>();
}
