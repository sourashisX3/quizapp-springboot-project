package com.sourashis.quizapp.modules.auth.entity;

import com.sourashis.quizapp.modules.roles.entity.Role;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    private String email;
    private String phoneNumber;
    private String address;
    private String profilePicture;

    @Column(length = 2000)
    private String authToken;

    @Column(length = 2000)
    private String refreshToken;
    private Integer scores;
}
