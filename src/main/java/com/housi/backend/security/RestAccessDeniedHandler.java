package com.housi.backend.security;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.housi.backend.service.audit.AuditLogger;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final AuditLogger auditLogger;

    public RestAccessDeniedHandler(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    @Override
    public void handle(
            HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
            throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth == null ? "anonymous" : auth.getName();
        auditLogger.accessDenied(email, request.getMethod(), request.getRequestURI());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Access denied\"}");
    }
}
