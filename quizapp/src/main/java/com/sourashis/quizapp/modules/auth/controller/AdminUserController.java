package com.sourashis.quizapp.modules.auth.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.core.response.PageableUtil;
import com.sourashis.quizapp.core.response.PaginationMeta;
import com.sourashis.quizapp.modules.auth.dto.AdminUserResponse;
import com.sourashis.quizapp.modules.auth.dto.AdminUserUpdateRequest;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.auth.exception.UserNotFoundException;
import com.sourashis.quizapp.modules.auth.repository.UserRepository;
import com.sourashis.quizapp.modules.analytics.repository.UserStatisticsRepository;
import com.sourashis.quizapp.modules.roles.entity.Role;
import com.sourashis.quizapp.modules.roles.exception.RoleNotFoundException;
import com.sourashis.quizapp.modules.roles.repository.RoleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Tag(name = "Admin Users", description = "Admin user management endpoints")
@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasAuthority('user:manage')")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatisticsRepository userStatisticsRepository;

    @Autowired
    private RoleRepository roleRepository;

    private static final Set<String> ALLOWED_SORT_PROPERTIES = new LinkedHashSet<>(
            java.util.List.of("id", "username", "email", "createdAt", "lastLoginAt", "accountStatus")
    );

    @Operation(summary = "Get all users", description = "Retrieves a paginated list of all users")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<Page<AdminUserResponse>>> getAllUsers(
            @Parameter(hidden = true) Pageable pageable) {
        Pageable safePageable = PageableUtil.safe(pageable, ALLOWED_SORT_PROPERTIES);
        Page<User> userPage = userRepository.findAll(safePageable);
        Page<AdminUserResponse> responsePage = userPage.map(this::toAdminUserResponse);
        return ApiResponseWrapper.paginated(
                responsePage,
                "Users retrieved successfully",
                PaginationMeta.of(responsePage)
        );
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a single user by their ID")
    @ApiResponse(responseCode = "200", description = "User retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<AdminUserResponse>> getUserById(
            @PathVariable @Parameter(description = "ID of the user") Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return ApiResponseWrapper.success(toAdminUserResponse(user), "User retrieved successfully");
    }

    @Operation(summary = "Update user", description = "Updates a user's details (role, status, etc.)")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<AdminUserResponse>> updateUser(
            @PathVariable @Parameter(description = "ID of the user") Long id,
            @RequestBody AdminUserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAccountStatus() != null) {
            user.setAccountStatus(request.getAccountStatus());
        }
        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new RoleNotFoundException(request.getRoleId().intValue()));
            user.setRole(role);
        }

        user = userRepository.save(user);
        return ApiResponseWrapper.success(toAdminUserResponse(user), "User updated successfully");
    }

    @Operation(summary = "Soft-delete user", description = "Disables a user account by setting status to DISABLED")
    @ApiResponse(responseCode = "200", description = "User disabled successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> deleteUser(
            @PathVariable @Parameter(description = "ID of the user to disable") Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setAccountStatus("DISABLED");
        userRepository.save(user);
        return ApiResponseWrapper.success(null, "User disabled successfully");
    }

    private AdminUserResponse toAdminUserResponse(User user) {
        Long totalXp = userStatisticsRepository.findByUserId(user.getId())
                .map(stats -> stats.getTotalXp())
                .orElse(0L);

        return AdminUserResponse.builder()
                .id(user.getId())
                .uuid(user.getUuid())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .accountStatus(user.getAccountStatus())
                .emailVerified(user.isEmailVerified())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .level(user.getLevel())
                .totalXp(totalXp)
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
