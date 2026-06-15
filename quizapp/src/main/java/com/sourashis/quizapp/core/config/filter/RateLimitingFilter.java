package com.sourashis.quizapp.core.config.filter;

import com.sourashis.quizapp.core.config.PublicPaths;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    @Value("${rate.limit.max-requests:100}")
    private int maxRequestsPerMinute;

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (stringRedisTemplate == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        String key = "rate:limit:" + clientIp;

        try {
            Long count = stringRedisTemplate.opsForValue().increment(key);
            if (count == null) {
                count = 1L;
            }

            if (count == 1) {
                stringRedisTemplate.expire(key, 60, TimeUnit.SECONDS);
            }

            if (count > maxRequestsPerMinute) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{" +
                        "\"statusCode\": 429," +
                        "\"message\": \"Too many requests. Please try again later.\"," +
                        "\"response\": null" +
                        "}");
                return;
            }
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String prefix : PublicPaths.RATE_LIMIT_SKIP_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}
