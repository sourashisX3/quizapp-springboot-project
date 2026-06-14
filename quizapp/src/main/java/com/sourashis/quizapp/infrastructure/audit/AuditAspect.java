package com.sourashis.quizapp.infrastructure.audit;

import com.sourashis.quizapp.core.audit.Auditable;
import com.sourashis.quizapp.modules.audit.entity.AuditLog;
import com.sourashis.quizapp.modules.audit.repository.AuditLogRepository;
import com.sourashis.quizapp.modules.auth.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        long start = System.currentTimeMillis();
        boolean isError = false;
        String errorMessage = null;

        try {
            return joinPoint.proceed();
        } catch (Throwable t) {
            isError = true;
            errorMessage = t.getMessage();
            throw t;
        } finally {
            try {
                long executionTime = System.currentTimeMillis() - start;
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                HttpServletRequest request = null;
                ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attrs != null) {
                    request = attrs.getRequest();
                }

                Long userId = null;
                String username = "anonymous";
                if (auth != null && auth.getPrincipal() instanceof User u) {
                    userId = u.getId();
                    username = u.getUsername();
                }

                AuditLog auditLog = AuditLog.builder()
                        .uuid(UUID.randomUUID().toString())
                        .userId(userId)
                        .username(username)
                        .action(auditable.action())
                        .resourceType(auditable.resourceType())
                        .ipAddress(request != null ? request.getRemoteAddr() : "unknown")
                        .userAgent(request != null ? request.getHeader("User-Agent") : null)
                        .httpMethod(request != null ? request.getMethod() : null)
                        .httpPath(request != null ? request.getRequestURI() : null)
                        .executionTimeMs((int) executionTime)
                        .isError(isError)
                        .errorMessage(errorMessage)
                        .build();

                auditLogRepository.save(auditLog);
            } catch (Exception e) {
                log.warn("Failed to persist audit log: {}", e.getMessage());
            }
        }
    }
}
