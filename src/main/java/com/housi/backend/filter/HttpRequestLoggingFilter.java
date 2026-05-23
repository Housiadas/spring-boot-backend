package com.housi.backend.filter;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.NonNull;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpRequestLoggingFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID = "requestId";

    private static final Logger log = LoggerFactory.getLogger("HTTP");

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        MDC.put(REQUEST_ID, UUID.randomUUID().toString());
        long startNanos = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - startNanos) / 1_000_000L;
            log.atInfo()
                    .setMessage("http.request")
                    .addKeyValue("event", "http.request")
                    .addKeyValue("request.method", request.getMethod())
                    .addKeyValue("request.path", request.getRequestURI())
                    .addKeyValue("request.query", request.getQueryString())
                    .addKeyValue("response.status", response.getStatus())
                    .addKeyValue("durationMs", durationMs)
                    .addKeyValue("remote.addr", request.getRemoteAddr())
                    .addKeyValue("user.agent", request.getHeader("User-Agent"))
                    .log();
            MDC.remove(REQUEST_ID);
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/docs")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui");
    }
}
