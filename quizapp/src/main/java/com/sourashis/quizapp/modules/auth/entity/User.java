package com.sourashis.quizapp.modules.auth.entity;

import com.sourashis.quizapp.modules.roles.entity.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uuid;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    private String displayName;

    private String phoneNumber;

    private String address;

    private String profilePictureUrl;

    private String bio;

    @Column(columnDefinition = "TEXT")
    private String socialLinksJson;

    @Builder.Default
    private Integer level = 1;

    @Builder.Default
    private Long currentXp = 0L;

    @Builder.Default
    private Long xpForNextLevel = 100L;

    @Builder.Default
    private String accountStatus = "ACTIVE";

    @Builder.Default
    private boolean emailVerified = false;

    private Instant lastLoginAt;

    private Instant createdAt;

    private Instant updatedAt;

    @Builder.Default
    private int version = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) {
            return java.util.Collections.emptySet();
        }
        return Stream.concat(
                Stream.of(new SimpleGrantedAuthority(role.getName())),
                role.getPermissions().stream()
                        .map(permission -> new SimpleGrantedAuthority(permission.getName()))
        ).collect(Collectors.toSet());
    }

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
