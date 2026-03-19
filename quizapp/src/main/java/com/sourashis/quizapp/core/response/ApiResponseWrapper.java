package com.sourashis.quizapp.core.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseWrapper<T> {

    private int statusCode;
    private String message;
    private T response;

    /**
     * Optional — present only on paginated endpoints, absent from JSON otherwise.
     */
    private PaginationMeta meta;

    // ── Non-paginated ─────────────────────────────────────────────────────────

    public static <T> ResponseEntity<ApiResponseWrapper<T>> success(T data, String message) {
        return ResponseEntity.ok(
                ApiResponseWrapper.<T>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(message)
                        .response(data)
                        .build());
    }

    public static <T> ResponseEntity<ApiResponseWrapper<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseWrapper.<T>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message(message)
                        .response(data)
                        .build());
    }

    public static <T> ResponseEntity<ApiResponseWrapper<T>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                ApiResponseWrapper.<T>builder()
                        .statusCode(status.value())
                        .message(message)
                        .build());
    }

    // ── Paginated ─────────────────────────────────────────────────────────────

    public static <T> ResponseEntity<ApiResponseWrapper<T>> paginated(T data, String message, PaginationMeta meta) {
        return ResponseEntity.ok(
                ApiResponseWrapper.<T>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(message)
                        .response(data)
                        .meta(meta)
                        .build());
    }
}

