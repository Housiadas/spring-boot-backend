package com.housi.backend.service.webhook;

import org.springframework.stereotype.Component;

import com.housi.backend.clients.http.WebhookSiteHttpClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookSiteService {

    private final WebhookSiteHttpClient client;

    public Mono<String> post(final Object request) {
        return this.client.post(request);
    }

    public Mono<String> postWithCircuitBreaker(final Object request) {
        return this.client.postWithCircuitBreaker(request);
    }
}
