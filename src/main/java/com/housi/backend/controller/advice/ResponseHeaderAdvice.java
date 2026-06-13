package com.housi.backend.controller.advice;

import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.housi.backend.constant.AppConstants;
import com.housi.backend.constant.AppHeaders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * GeneralControllerAdvice
 *
 * This response advice is used to inject the api response time
 * within response header
 *
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ResponseHeaderAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(
            @NonNull final MethodParameter returnType,
            @NonNull final Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            final Object body,
            @NonNull final MethodParameter methodParameter,
            @NonNull final MediaType mediaType,
            @NonNull final Class<? extends HttpMessageConverter<?>> aClass,
            @NonNull final ServerHttpRequest request,
            @NonNull final ServerHttpResponse response) {

        final ServletServerHttpRequest servletServerRequest = (ServletServerHttpRequest) request;
        ResponseHeaderAdvice.addResponseTimeHeader(response, servletServerRequest);

        return body;
    }

    private static void addResponseTimeHeader(
            final ServerHttpResponse response,
            final ServletServerHttpRequest servletServerRequest) {

        final Long startTime =
                (Long) servletServerRequest.getServletRequest().getAttribute(AppConstants.API_TIME);
        if (startTime != null) {
            response.getHeaders().add(AppHeaders.RESPONSE_TIME_HEADER, fromTimeToString(startTime));
        }
    }

    private static String fromTimeToString(final Long startTime) {
        final long elapsed = System.nanoTime() - startTime;
        final long millis = elapsed / 1_000_000;
        return millis > 0 ? millis + " ms" : elapsed + " ns";
    }
}
