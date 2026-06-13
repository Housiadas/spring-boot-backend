package com.housi.backend.configuration;

import com.housi.backend.controller.interceptor.LogSlowResponseTimeInterceptor;
import com.housi.backend.controller.interceptor.TimeExecutionInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Value("${miscellaneous.max-response-time-to-log-in-ms}")
    private int maxResponseTimeToLogInMs;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new TimeExecutionInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new LogSlowResponseTimeInterceptor(this.maxResponseTimeToLogInMs))
                .addPathPatterns("/**");
    }
}
