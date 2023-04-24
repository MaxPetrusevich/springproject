package com.spring.springproject.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data

@EqualsAndHashCode(of = {"id"})
@ToString(exclude = {"techniques"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Model implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Integer id;

    @Column(name = "model_name")
    private String name;

    @OneToMany(mappedBy = "model")
    private Set<Technique> techniques = new HashSet<Technique>();
}
