package com.sourashis.quizapp.modules.auth.service;

import com.sourashis.quizapp.core.config.utils.JwtUtil;
import com.sourashis.quizapp.modules.auth.dto.AuthenticationRequest;
import com.sourashis.quizapp.modules.auth.dto.AuthenticationResponse;
import com.sourashis.quizapp.modules.auth.entity.RefreshToken;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.auth.exception.UserNotFoundException;
import com.sourashis.quizapp.modules.auth.exception.UsernameAlreadyExistsException;
import com.sourashis.quizapp.modules.auth.mapper.AuthenticationMapper;
import com.sourashis.quizapp.modules.auth.repository.UserRepository;
import com.sourashis.quizapp.modules.roles.entity.Permission;
import com.sourashis.quizapp.modules.roles.entity.Role;
import com.sourashis.quizapp.modules.roles.exception.RoleNotFoundException;
import com.sourashis.quizapp.modules.roles.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

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

    public AuthenticationResponse register(AuthenticationRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new UsernameAlreadyExistsException("Username '" + req.getUsername() + "' is already taken");
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new UsernameAlreadyExistsException("Email '" + req.getEmail() + "' is already registered");
        }

        Role role = resolveRole(req.getRoleName());

        User user = AuthenticationMapper.toUserEntity(req);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(role);
        user = userRepository.save(user);

        Set<String> permissionNames = extractPermissionNames(user.getRole());
        String roleName = user.getRole() != null ? user.getRole().getName() : null;
        String authToken = jwtUtil.generateAccessToken(user.getUsername(), user.getId(), roleName, permissionNames);
        String rawRefreshToken = refreshTokenService.createRefreshToken(user);

        return AuthenticationMapper.toUserResponse(user, authToken, rawRefreshToken);
    }

    public AuthenticationResponse login(AuthenticationRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new UserNotFoundException(req.getUsername()));

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        Set<String> permissionNames = extractPermissionNames(user.getRole());
        String roleName = user.getRole() != null ? user.getRole().getName() : null;
        String authToken = jwtUtil.generateAccessToken(user.getUsername(), user.getId(), roleName, permissionNames);
        String rawRefreshToken = refreshTokenService.createRefreshToken(user);

        return AuthenticationMapper.toUserResponse(user, authToken, rawRefreshToken);
    }

    public AuthenticationResponse refreshAccessToken(String token) {
        RefreshToken storedToken = refreshTokenService.validateRefreshToken(token);
        User user = storedToken.getUser();

        refreshTokenService.revokeRefreshToken(token);

        Set<String> permissionNames = extractPermissionNames(user.getRole());
        String roleName = user.getRole() != null ? user.getRole().getName() : null;
        String authToken = jwtUtil.generateAccessToken(user.getUsername(), user.getId(), roleName, permissionNames);
        String rawRefreshToken = refreshTokenService.createRefreshToken(user);

        return AuthenticationMapper.toUserResponse(user, authToken, rawRefreshToken);
    }

    public void logout(String token) {
        refreshTokenService.revokeRefreshToken(token);
    }

    private Role resolveRole(String roleName) {
        String name;
        if (roleName != null && !roleName.isBlank()) {
            name = roleName;
        } else {
            name = "ROLE_USER";
        }
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException(name));
    }

    private Set<String> extractPermissionNames(Role role) {
        if (role == null || role.getPermissions() == null) {
            return java.util.Collections.emptySet();
        }
        return role.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }
}
