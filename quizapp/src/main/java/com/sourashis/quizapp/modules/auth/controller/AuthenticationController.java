package com.sourashis.quizapp.modules.auth.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.auth.dto.AuthenticationRequest;
import com.sourashis.quizapp.modules.auth.dto.AuthenticationResponse;
import com.sourashis.quizapp.modules.auth.dto.RefreshTokenRequest;
import com.sourashis.quizapp.modules.auth.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * POST /auth/register
     * User registration
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponseWrapper<AuthenticationResponse>> register(
            @RequestBody @Valid AuthenticationRequest request,
            @RequestParam(value = "admin", defaultValue = "false") boolean admin
    ) {
        return ApiResponseWrapper.created(authenticationService.register(request, admin), "Registration successful!");
    }

    /**
     * POST /auth/login
     * User login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseWrapper<AuthenticationResponse>> login(@RequestBody @Valid AuthenticationRequest request) {
        return ApiResponseWrapper.success(authenticationService.login(request), "Login successful!");
    }

    /**
     * POST /auth/refresh
     * Refresh access token using refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseWrapper<AuthenticationResponse>> refresh(
            @RequestBody @Valid RefreshTokenRequest request
    ) {
        return ApiResponseWrapper.success(authenticationService.refreshAccessToken(request.getRefreshToken()), "Token refreshed successfully!");
    }

    /**
     * POST /auth/logout
     * Logout and revoke refresh token
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
