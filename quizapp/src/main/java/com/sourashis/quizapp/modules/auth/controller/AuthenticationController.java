package com.sourashis.quizapp.modules.auth.controller;

import com.sourashis.quizapp.core.audit.Auditable;
import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.auth.dto.AuthenticationRequest;
import com.sourashis.quizapp.modules.auth.dto.AuthenticationResponse;
import com.sourashis.quizapp.modules.auth.dto.OnLogin;
import com.sourashis.quizapp.modules.auth.dto.OnRegister;
import com.sourashis.quizapp.modules.auth.dto.RefreshTokenRequest;
import com.sourashis.quizapp.modules.auth.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Register, login, refresh tokens, logout")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService;

    @Auditable(action = "REGISTER", resourceType = "USER")
    @Operation(summary = "Register a new user account", description = "Creates a new user account with the provided credentials. Accepts username, password, and email as required fields. Optionally accepts phoneNumber, address, displayName, and roleId. Returns JWT tokens for immediate authentication. Passwords are hashed with BCrypt before storage.")
    @ApiResponse(responseCode = "201", description = "Registration successful. Returns auth tokens and user details.")
    @ApiResponse(responseCode = "400", description = "Validation failed — missing required fields (username, password, email) or invalid format")
    @ApiResponse(responseCode = "409", description = "Username or email already exists")
    @ApiResponse(responseCode = "422", description = "Password does not meet security requirements")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping("/register")
    public ResponseEntity<ApiResponseWrapper<AuthenticationResponse>> register(
            @RequestBody @Validated(OnRegister.class) @Parameter(description = "Registration request body containing username, password, email (required) and optionally phoneNumber, address, displayName, roleId", required = true) AuthenticationRequest request) {
        AuthenticationResponse response = authService.register(request);
        return ApiResponseWrapper.created(response, "Registration successful");
    }

    @Auditable(action = "LOGIN", resourceType = "USER")
    @Operation(summary = "Authenticate a user and receive JWT tokens", description = "Authenticates using username/email and password. On success, returns an access token (JWT) for API authorization and a refresh token for obtaining new access tokens. The access token expires based on configured TTL.")
    @ApiResponse(responseCode = "200", description = "Login successful. Returns auth tokens and user details.")
    @ApiResponse(responseCode = "401", description = "Invalid credentials — username or password is incorrect")
    @ApiResponse(responseCode = "403", description = "Account is disabled or locked")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping("/login")
    public ResponseEntity<ApiResponseWrapper<AuthenticationResponse>> login(
            @RequestBody @Validated(OnLogin.class) @Parameter(description = "Login request body containing username/email and password", required = true) AuthenticationRequest request) {
        AuthenticationResponse response = authService.login(request);
        return ApiResponseWrapper.success(response, "Login successful");
    }

    @Auditable(action = "READ", resourceType = "USER")
    @Operation(summary = "Refresh an expired access token", description = "Accepts a valid refresh token and returns a new access token and a new refresh token. The old refresh token is invalidated. Refresh tokens have a longer lifespan than access tokens.")
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully. Returns new auth tokens.")
    @ApiResponse(responseCode = "400", description = "Invalid or expired refresh token")
    @ApiResponse(responseCode = "401", description = "Refresh token has been revoked")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseWrapper<AuthenticationResponse>> refresh(
            @RequestBody @Valid @Parameter(description = "Refresh token request body containing the refresh token string", required = true) RefreshTokenRequest request) {
        AuthenticationResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return ApiResponseWrapper.success(response, "Token refreshed successfully");
    }

    @Auditable(action = "LOGOUT", resourceType = "USER")
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Logout and invalidate refresh token", description = "Invalidates the provided refresh token, effectively logging the user out. Requires authentication. After logout, the refresh token can no longer be used to obtain new access tokens.")
    @ApiResponse(responseCode = "200", description = "Logged out successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required — valid JWT access token must be provided")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<ApiResponseWrapper<Void>> logout(
            @RequestBody @Valid @Parameter(description = "Refresh token request body containing the refresh token to invalidate", required = true) RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ApiResponseWrapper.success(null, "Logged out successfully");
    }
}
