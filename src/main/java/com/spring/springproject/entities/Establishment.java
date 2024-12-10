package com.spring.springproject.entities;

import javax.persistence.*;

import lombok.*;

import java.util.List;

@Entity
@Table(name = "establishment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"services"})
@ToString(exclude = {"services"})
public class Establishment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "phone", nullable = false, length = 19)  // Формат: +375(XX)XXX-XX-XX
    private String phone;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "description", length = 1000)
    private String description;

    @OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL)
    private List<GovService> services;
}
