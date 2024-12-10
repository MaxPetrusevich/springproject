package com.spring.springproject.entities;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name = "citizen")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"user"})
@ToString(exclude = {"user"})
public class Citizen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "middle_name", length = 50)
    private String middleName;

    @Column(name = "phone", nullable = false, length = 19)
    private String phone;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "identify_number", nullable = false, length = 14, unique = true)
    private String identifyNumber;

    @Column(name = "passport_series", nullable = false, length = 2)
    private String passportSeries;

    @Column(name = "passport_number", nullable = false, length = 7)
    private String passportNumber;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
}
