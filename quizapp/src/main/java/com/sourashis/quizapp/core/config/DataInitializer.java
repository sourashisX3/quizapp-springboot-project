package com.sourashis.quizapp.core.config;

import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.auth.repository.AuthenticationRepository;
import com.sourashis.quizapp.modules.roles.entity.Permission;
import com.sourashis.quizapp.modules.roles.entity.Role;
import com.sourashis.quizapp.modules.roles.repository.PermissionRepository;
import com.sourashis.quizapp.modules.roles.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private PermissionRepository permissionRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private AuthenticationRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (permissionRepository.count() > 0) {
            log.info("Data already initialized, skipping seeder.");
            return;
        }

        log.info("Seeding permissions...");
        Map<String, String> permissionDefs = new LinkedHashMap<>();
        permissionDefs.put("question:read", "View questions");
        permissionDefs.put("question:create", "Create questions");
        permissionDefs.put("question:update", "Update questions");
        permissionDefs.put("question:delete", "Delete questions");
        permissionDefs.put("quiz:read", "View quizzes");
        permissionDefs.put("quiz:create", "Create quizzes");
        permissionDefs.put("quiz:update", "Update quizzes");
        permissionDefs.put("quiz:delete", "Delete quizzes");
        permissionDefs.put("quiz:attempt", "Attempt and submit quizzes");
        permissionDefs.put("category:read", "View categories");
        permissionDefs.put("category:create", "Create categories");
        permissionDefs.put("category:update", "Update categories");
        permissionDefs.put("category:delete", "Delete categories");
        permissionDefs.put("user:read", "View users");
        permissionDefs.put("user:create", "Create users");
        permissionDefs.put("user:update", "Update users");
        permissionDefs.put("user:delete", "Delete users");
        permissionDefs.put("user:manage", "Manage user roles");
        permissionDefs.put("role:read", "View roles");
        permissionDefs.put("role:create", "Create roles");
        permissionDefs.put("role:update", "Update roles");
        permissionDefs.put("role:delete", "Delete roles");

        List<Permission> permissions = permissionDefs.entrySet().stream()
                .map(entry -> Permission.builder().name(entry.getKey()).description(entry.getValue()).build())
                .peek(p -> log.debug("  Creating permission: {}", p.getName()))
                .collect(Collectors.toList());

        permissionRepository.saveAll(permissions);
        log.info("Created {} permissions.", permissions.size());

        Map<String, Permission> permMap = permissions.stream()
                .collect(Collectors.toMap(Permission::getName, p -> p));

        log.info("Seeding roles...");

        Set<Permission> userPerms = Set.of(
                permMap.get("question:read"),
                permMap.get("quiz:read"),
                permMap.get("quiz:attempt"),
                permMap.get("category:read")
        );

        Set<Permission> adminPerms = Set.of(
                permMap.get("question:read"), permMap.get("question:create"), permMap.get("question:update"), permMap.get("question:delete"),
                permMap.get("quiz:read"), permMap.get("quiz:create"), permMap.get("quiz:update"), permMap.get("quiz:delete"), permMap.get("quiz:attempt"),
                permMap.get("category:read"), permMap.get("category:create"), permMap.get("category:update"), permMap.get("category:delete"),
                permMap.get("user:read"), permMap.get("user:create"), permMap.get("user:update"), permMap.get("user:delete"),
                permMap.get("role:read")
        );

        Set<Permission> superAdminPerms = Set.copyOf(permMap.values());

        Role userRole = Role.builder().name("ROLE_USER").description("Regular user").permissions(userPerms).build();
        Role adminRole = Role.builder().name("ROLE_ADMIN").description("Administrator").permissions(adminPerms).build();
        Role superAdminRole = Role.builder().name("ROLE_SUPER_ADMIN").description("Super Administrator").permissions(superAdminPerms).build();

        roleRepository.saveAll(List.of(userRole, adminRole, superAdminRole));
        log.info("Created 3 roles: ROLE_USER, ROLE_ADMIN, ROLE_SUPER_ADMIN");

        if (!userRepository.existsByUsername("superadmin")) {
            User superUser = new User();
            superUser.setUsername("superadmin");
            superUser.setPassword(passwordEncoder.encode("superadmin123"));
            superUser.setRole(superAdminRole);
            superUser.setEmail("superadmin@quizapp.com");
            superUser.setPhoneNumber("0000000000");
            superUser.setAddress("System");
            superUser.setProfilePicture("");
            userRepository.save(superUser);
            log.info("Created default superadmin user (username: superadmin, password: superadmin123)");
        }
    }
}
