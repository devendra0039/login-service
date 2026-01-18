package com.addrs.addrs_user_management_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user", indexes = { @Index(name = "id_email", columnList = "email")})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    @NonNull
    private String email;
    private Long mobileNumber;
    private String password;
    private String role;
    private Boolean active;

}

