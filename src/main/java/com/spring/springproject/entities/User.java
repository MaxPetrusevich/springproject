package com.spring.springproject.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"citizen", "role"})
@EqualsAndHashCode(exclude = {"citizen", "role"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identify_number", nullable = false, length = 14, unique = true)
    private String identifyNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "image")
    private byte[] image;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Citizen citizen;
}
