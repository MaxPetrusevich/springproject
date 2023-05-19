package com.spring.springproject.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Model implements Serializable {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Integer id;

    @EqualsAndHashCode.Exclude
    @Column(name = "model_name")
    private String name;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "model")
    private Set<Technique> techniques = new HashSet<Technique>();
}
