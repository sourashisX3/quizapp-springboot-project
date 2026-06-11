package com.sourashis.quizapp.modules.auth.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.auth.dto.*;
import com.sourashis.quizapp.modules.auth.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Authentication operations.
 * Handles user registration, login, token refresh, and logout.
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * POST /auth/register
     * Registers a new user account.
     * Default role is ROLE_USER. Pass ?admin=true for ROLE_ADMIN, or ?role=ROLE_X for a custom role.
     * Access: Public (no authentication required)
     *
     * @param request AuthenticationRequest with username, password, email, phoneNumber, address
     * @param admin   optional query param (default: false) — shortcut for ROLE_ADMIN
     * @param role    optional query param — custom role name to assign (e.g. ?role=ROLE_MODERATOR)
     * @return ResponseEntity with user details and JWT tokens
     * @throws com.sourashis.quizapp.modules.auth.exception.UsernameAlreadyExistsException if username is taken
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponseWrapper<AuthenticationResponse>> register(
            @RequestBody @Validated(OnRegister.class) AuthenticationRequest request,
            @RequestParam(value = "admin", defaultValue = "false") boolean admin,
            @RequestParam(value = "role", defaultValue = "") String role
    ) {
        return ApiResponseWrapper.created(
                authenticationService.register(request, admin, role), "Registration successful!");
    }

    /**
     * POST /auth/login
     * Authenticates an existing user and returns JWT tokens.
     * Access: Public (no authentication required)
     *
     * @param request AuthenticationRequest with username and password
     * @return ResponseEntity with user details and JWT tokens
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseWrapper<AuthenticationResponse>> login(
            @RequestBody @Validated(OnLogin.class) AuthenticationRequest request
    ) {
        return ApiResponseWrapper.success(authenticationService.login(request), "Login successful!");
    }

    /**
     * POST /auth/refresh
     * Refreshes the access token using a valid refresh token.
     * Rotates refresh token for security (old one is revoked).
     * Access: Public (no authentication required — uses refresh token)
     *
     * @param request RefreshTokenRequest with the refresh token
     * @return ResponseEntity with new JWT tokens
     * @throws com.sourashis.quizapp.modules.auth.exception.InvalidRefreshTokenException if token is invalid or expired
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseWrapper<AuthenticationResponse>> refresh(
            @RequestBody @Valid RefreshTokenRequest request
    ) {
        return ApiResponseWrapper.success(
                authenticationService.refreshAccessToken(request.getRefreshToken()),
                "Token refreshed successfully!");
    }

    /**
     * POST /auth/logout
     * Logs out the user by revoking their refresh token.
     * Access: Authenticated users only
     *
     * @param request RefreshTokenRequest with the refresh token to revoke
     * @return ResponseEntity with success message
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<Void>> logout(
            @RequestBody @Valid RefreshTokenRequest request
    ) {
        authenticationService.logout(request.getRefreshToken());
        return ApiResponseWrapper.success(null, "Logout successful!");
    }
}
