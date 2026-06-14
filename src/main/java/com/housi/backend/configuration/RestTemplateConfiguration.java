package com.housi.backend.configuration;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder builder) {
        return builder.build();
    }
}
