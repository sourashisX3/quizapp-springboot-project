package com.sourashis.quizapp.core.exception;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // ── Catch-all fallback ────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        return ApiResponseWrapper.error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }
}

