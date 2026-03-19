package com.sourashis.quizapp.modules.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;


    private String email;
    private String phoneNumber;
    private String address;
    private String profilePicture;
    private String authToken;
    private String refreshToken;
}
