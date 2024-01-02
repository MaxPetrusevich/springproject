package com.spring.springproject.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Technique implements Serializable {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tech_id")
    private Integer id;

    @EqualsAndHashCode.Exclude
    @Column(name = "price")
    private Double price;

    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "tech_store",
            joinColumns = @JoinColumn(name = "tech_id"),
            inverseJoinColumns = @JoinColumn(name = "store_id"))
    @EqualsAndHashCode.Exclude
    private Set<Store> storeList = new HashSet<Store>();
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "producer_id")
    private Producer producer;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "model_id")
    private Model model;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
    @ToString.Exclude
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "technique", cascade = CascadeType.ALL)
    private Set<OrderTechnique> orderTechniques = new HashSet<>();
    @Column(name = "image")
    private String image;
}
