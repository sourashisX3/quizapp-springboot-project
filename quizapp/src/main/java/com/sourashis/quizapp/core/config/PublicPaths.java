package com.sourashis.quizapp.core.config;

public final class PublicPaths {

    private PublicPaths() {}

    public static final String[] NO_AUTH_PATTERNS = {
        "/api/v1/auth/**",
        "/ws/**",
        "/actuator/health",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-ui.html"
    };

    public static final String[] PUBLIC_GET_PATTERNS = {
        "/api/v1/categories/**",
        "/api/v1/quizzes/recent",
        "/api/v1/quizzes/category/**",
        "/api/v1/quizzes/trending-categories",
        "/api/v1/quizzes/search",
        "/api/v1/contests/all",
        "/api/v1/contests",
        "/api/v1/contests/upcoming",
        "/api/v1/contests/completed"
    };

    public static final String[] RATE_LIMIT_SKIP_PREFIXES = {
        "/api/v1/auth/",
        "/ws/",
        "/actuator/",
        "/swagger-ui",
        "/v3/api-docs",
        "/swagger-ui.html"
    };
}
