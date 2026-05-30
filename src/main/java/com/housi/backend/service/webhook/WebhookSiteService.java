package com.housi.backend.service.webhook;

import reactor.core.publisher.Mono;

public interface WebhookSiteService {
    Mono<String> post(Object request);

    Mono<String> postWithCircuitBreaker(Object request);
}
