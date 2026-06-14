package com.sourashis.quizapp.modules.auth.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.auth.dto.AuthenticationRequest;
import com.sourashis.quizapp.modules.auth.dto.AuthenticationResponse;
import com.sourashis.quizapp.modules.auth.dto.OnLogin;
import com.sourashis.quizapp.modules.auth.dto.OnRegister;
import com.sourashis.quizapp.modules.auth.dto.RefreshTokenRequest;
import com.sourashis.quizapp.modules.auth.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseWrapper<AuthenticationResponse>> register(
            @RequestBody @Validated(OnRegister.class) AuthenticationRequest request) {
        AuthenticationResponse response = authService.register(request);
        return ApiResponseWrapper.created(response, "Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseWrapper<AuthenticationResponse>> login(
            @RequestBody @Validated(OnLogin.class) AuthenticationRequest request) {
        AuthenticationResponse response = authService.login(request);
        return ApiResponseWrapper.success(response, "Login successful");
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseWrapper<AuthenticationResponse>> refresh(
            @RequestBody @Valid RefreshTokenRequest request) {
        AuthenticationResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return ApiResponseWrapper.success(response, "Token refreshed successfully");
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<Void>> logout(
            @RequestBody @Valid RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ApiResponseWrapper.success(null, "Logged out successfully");
    }
}
