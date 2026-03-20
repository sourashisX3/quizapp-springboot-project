package com.sourashis.quizapp.core.exception;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.auth.exception.InvalidRefreshTokenException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ── Handles all custom module exceptions (QuestionNotFoundException, QuizNotFoundException, etc.) ──

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleBaseException(BaseException ex) {
        log.error("Application exception [{}]: {}", ex.getStatus(), ex.getMessage());
        return ApiResponseWrapper.error(ex.getStatus(), ex.getMessage());
    }

    // ── Handles @Valid / @Validated bean validation failures ──────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.error("Validation failed: {}", errors);
        return ApiResponseWrapper.error(HttpStatus.BAD_REQUEST, "Validation failed: " + errors);
    }

    // ── Handles authorization exceptions ──────────────────────────────────────

    /**
     * Handles @PreAuthorize access denied failures.
     * This is thrown when a user doesn't have the required role for an endpoint.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage());
        return ApiResponseWrapper.error(
                HttpStatus.FORBIDDEN,
                "Access Denied - You do not have the required permissions to access this resource. ADMIN role required."
        );
    }

    // ── Handles authentication exceptions ──────────────────────────────────────

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("Authentication failed: Invalid credentials");
        return ApiResponseWrapper.error(HttpStatus.UNAUTHORIZED, "Invalid username or password!");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        return ApiResponseWrapper.error(HttpStatus.UNAUTHORIZED, "User not found: " + ex.getMessage());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleDisabledException(DisabledException ex) {
        log.error("Account disabled: {}", ex.getMessage());
        return ApiResponseWrapper.error(HttpStatus.UNAUTHORIZED, "User account is disabled!");
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleLockedException(LockedException ex) {
        log.error("Account locked: {}", ex.getMessage());
        return ApiResponseWrapper.error(HttpStatus.UNAUTHORIZED, "User account is locked!");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleAuthenticationException(AuthenticationException ex) {
        log.error("Authentication failed: {}", ex.getMessage());
        return ApiResponseWrapper.error(HttpStatus.UNAUTHORIZED, "Authentication failed: " + ex.getMessage());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex) {
        log.error("Invalid refresh token: {}", ex.getMessage());
        return ApiResponseWrapper.error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleJwtException(JwtException ex) {
        log.error("JWT exception: {}", ex.getMessage());
        return ApiResponseWrapper.error(HttpStatus.UNAUTHORIZED, "Invalid or expired token!");
    }

    // ── Catch-all fallback ────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        return ApiResponseWrapper.error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }
}

