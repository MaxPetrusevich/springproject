package com.spring.springproject.entities;

import javax.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Type implements Serializable {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer id;

    @Column(name = "type_name")
    private String name;
    @EqualsAndHashCode.Exclude
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
}
