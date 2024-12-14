package com.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "organisations")
@Data
public class Organisation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;

    private Boolean active = true;
    @OneToMany(mappedBy = "organisation")
    private List<Product> products;
    
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        active = true;
        createdAt = LocalDateTime.now();
    }
} 