package com.housi.backend.controller.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jspecify.annotations.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import com.housi.backend.constant.AppConstants;

public class TimeExecutionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final Object handler) {
        final long nano = System.nanoTime();

        request.setAttribute(AppConstants.API_TIME, nano);
        return true;
    }
}
