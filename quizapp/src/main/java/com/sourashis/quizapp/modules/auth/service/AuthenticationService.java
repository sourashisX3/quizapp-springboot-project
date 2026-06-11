package com.sourashis.quizapp.modules.auth.service;

import com.sourashis.quizapp.core.config.utils.JwtUtil;
import com.sourashis.quizapp.modules.auth.dto.AuthenticationRequest;
import com.sourashis.quizapp.modules.auth.dto.AuthenticationResponse;
import com.sourashis.quizapp.modules.auth.entity.RefreshToken;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.auth.exception.InvalidRefreshTokenException;
import com.sourashis.quizapp.modules.auth.exception.UsernameAlreadyExistsException;
import com.sourashis.quizapp.modules.auth.mapper.AuthenticationMapper;
import com.sourashis.quizapp.modules.auth.repository.AuthenticationRepository;
import com.sourashis.quizapp.modules.roles.entity.Role;
import com.sourashis.quizapp.modules.roles.exception.RoleNotFoundException;
import com.sourashis.quizapp.modules.roles.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

/**
 * Service layer for Authentication operations.
 * Handles user registration, login, token management, and logout.
 * Coordinates with RoleRepository for role-based access assignment.
 */
@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    /**
     * Register a new user with a single role.
     * If roleName is provided, uses that role; otherwise falls back to
     * the asAdmin boolean (ROLE_ADMIN if true, ROLE_USER if false).
     * Generates access token and refresh token upon successful registration.
     *
     * @param request  the registration request containing user details
     * @param asAdmin  shortcut flag — assigns ROLE_ADMIN if true, ROLE_USER if false (ignored if roleName provided)
     * @param roleName optional custom role name to assign
     * @return AuthenticationResponse with user details and JWT tokens
     * @throws UsernameAlreadyExistsException if the username is already taken
     * @throws RoleNotFoundException if the specified role is not found
     */
    public AuthenticationResponse register(AuthenticationRequest request, boolean asAdmin, String roleName) {
        if (repository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username '" + request.getUsername() + "' already exists!", null);
        }

        Role role = resolveRole(asAdmin, roleName);

        User user = AuthenticationMapper.toUserEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setEmail(request.getEmail());
        user.setProfilePicture("");
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());

        repository.save(user);

        Set<String> permissionNames = extractPermissionNames(role);
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), role.getName(), permissionNames);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        user.setAuthToken(accessToken);
        user.setRefreshToken(refreshToken.getToken());
        repository.save(user);

        return AuthenticationMapper.toUserResponse(user);
    }

    /**
     * Authenticate a user and generate JWT tokens.
     * Validates credentials via AuthenticationManager and issues
     * access and refresh tokens on successful authentication.
     *
     * @param request the login request with username and password
     * @return AuthenticationResponse with user details and JWT tokens
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are invalid
     */
    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        String roleName = user.getRole() != null ? user.getRole().getName() : null;
        Set<String> permissionNames = extractPermissionNames(user.getRole());
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), roleName, permissionNames);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        user.setAuthToken(accessToken);
        user.setRefreshToken(refreshToken.getToken());
        repository.save(user);

        return AuthenticationMapper.toUserResponse(user);
    }

    /**
     * Refresh the access token using a valid refresh token.
     * Validates the refresh token, generates new access and refresh tokens,
     * and revokes the old refresh token (token rotation).
     *
     * @param refreshTokenValue the refresh token string
     * @return AuthenticationResponse with new JWT tokens
     * @throws InvalidRefreshTokenException if the refresh token is invalid or expired
     */
    public AuthenticationResponse refreshAccessToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid or expired refresh token!"));

        User user = refreshToken.getUser();

        String roleName = user.getRole() != null ? user.getRole().getName() : null;
        Set<String> permissionNames = extractPermissionNames(user.getRole());
        String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), roleName, permissionNames);

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
        refreshTokenService.revokeRefreshToken(refreshTokenValue);

        user.setAuthToken(newAccessToken);
        user.setRefreshToken(newRefreshToken.getToken());
        repository.save(user);

        return AuthenticationMapper.toUserResponse(user);
    }

    /**
     * Logout a user by revoking their refresh token.
     *
     * @param refreshTokenValue the refresh token to revoke
     */
    public void logout(String refreshTokenValue) {
        refreshTokenService.revokeRefreshToken(refreshTokenValue);
    }

    /**
     * Resolve the Role entity from the provided parameters.
     * If roleName is provided, looks it up in the database.
     * Otherwise falls back to the asAdmin boolean for default role assignment.
     *
     * @param asAdmin  shortcut flag for default role assignment
     * @param roleName optional custom role name
     * @return Role entity
     */
    private Role resolveRole(boolean asAdmin, String roleName) {
        if (roleName != null && !roleName.isBlank()) {
            return roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RoleNotFoundException(roleName));
        }
        String defaultRole = asAdmin ? "ROLE_ADMIN" : "ROLE_USER";
        return roleRepository.findByName(defaultRole)
                .orElseThrow(() -> new RoleNotFoundException(defaultRole));
    }

    /**
     * Extract all permission names from a Role entity.
     *
     * @param role the role entity
     * @return Set of permission name strings
     */
    private Set<String> extractPermissionNames(Role role) {
        if (role == null || role.getPermissions() == null) return Collections.emptySet();
        return role.getPermissions().stream()
                .map(perm -> perm.getName())
                .collect(java.util.stream.Collectors.toSet());
    }
}
