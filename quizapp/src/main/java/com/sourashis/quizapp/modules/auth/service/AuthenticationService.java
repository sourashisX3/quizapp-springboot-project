package com.sourashis.quizapp.modules.auth.service;

import com.sourashis.quizapp.core.config.utils.JwtUtil;
import com.sourashis.quizapp.modules.auth.dto.AuthenticationRequest;
import com.sourashis.quizapp.modules.auth.dto.AuthenticationResponse;
import com.sourashis.quizapp.modules.auth.entity.RefreshToken;
import com.sourashis.quizapp.modules.auth.entity.Role;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.auth.exception.InvalidRefreshTokenException;
import com.sourashis.quizapp.modules.auth.exception.UsernameAlreadyExistsException;
import com.sourashis.quizapp.modules.auth.mapper.AuthenticationMapper;
import com.sourashis.quizapp.modules.auth.repository.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    /**
     * Register a new user
     */
    public AuthenticationResponse register(AuthenticationRequest request, boolean asAdmin) {
        if (repository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username '" + request.getUsername() + "' already exists!", null);
        }

        User user = AuthenticationMapper.toUserEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(asAdmin ? Role.ROLE_ADMIN : Role.ROLE_USER);
        user.setEmail(request.getEmail());
        user.setProfilePicture("");
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());

        repository.save(user);

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole().name());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        user.setAuthToken(accessToken);
        user.setRefreshToken(refreshToken.getToken());
        repository.save(user);

        return AuthenticationMapper.toUserResponse(user);
    }

    /**
     * Login user and generate tokens
     */
    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole().name());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        user.setAuthToken(accessToken);
        user.setRefreshToken(refreshToken.getToken());
        repository.save(user);

        return AuthenticationMapper.toUserResponse(user);
    }

    /**
     * Refresh access token using refresh token
     */
    public AuthenticationResponse refreshAccessToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid or expired refresh token!"));

        User user = refreshToken.getUser();

        // Generate new access token
        String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole().name());

        // Rotate refresh token (create new one and revoke old)
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
        refreshTokenService.revokeRefreshToken(refreshTokenValue);

        user.setAuthToken(newAccessToken);
        user.setRefreshToken(newRefreshToken.getToken());
        repository.save(user);

        return AuthenticationMapper.toUserResponse(user);
    }

    /**
     * Logout user and revoke refresh token
     */
    public void logout(String refreshTokenValue) {
        refreshTokenService.revokeRefreshToken(refreshTokenValue);
    }
}
