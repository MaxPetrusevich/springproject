package com.example.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private String category;
    private boolean active = true;
    
    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "organization_id")
    private Organisation organisation;
    
    @OneToMany(mappedBy = "product")
    @ToString.Exclude
    private List<SubscriptionPlan> subscriptionPlans;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 