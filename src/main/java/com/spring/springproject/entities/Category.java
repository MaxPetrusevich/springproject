package com.spring.springproject.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString(exclude = {"techniques", "types"})
@AllArgsConstructor
@Data
@Builder
@Entity
@Table
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id;

    @Column(name = "category_name")
    private String name;

    @OneToMany(mappedBy = "category")
    private Set<Type> types = new HashSet<Type>();

    @OneToMany(mappedBy = "category")
    private Set<Technique> techniques = new HashSet<Technique>();
}
