package com.housi.backend.infrastructure.client.http;

import static com.housi.backend.utils.WebClientUtils.getErrorMessage;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.housi.backend.utils.WebClientUtils;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class WebhookSiteHttpClient {

    private static final String NAME = "webhookSite";
    private static final String POST_PATH = "/";

    private final WebClient webClient;
    private final CircuitBreaker defaultCircuitBreaker;

    public WebhookSiteHttpClient(
            @Value("${http.clients.webhook-site.base-url}") final String baseUrl,
            @Value("${http.clients.default-timeout}") final Integer timeOutInMs,
            final WebClient.Builder builder) {
        this.webClient = WebClientUtils.createWebClient(builder, baseUrl, timeOutInMs, NAME);

        this.defaultCircuitBreaker =
                CircuitBreaker.of(
                        NAME,
                        CircuitBreakerConfig.custom()
                                .slidingWindowSize(10)
                                .slidingWindowType(SlidingWindowType.COUNT_BASED)
                                .failureRateThreshold(70.0f)
                                .slowCallDurationThreshold(Duration.ofSeconds(3))
                                .slowCallRateThreshold(70.0f)
                                .waitDurationInOpenState(Duration.ofSeconds(10))
                                .permittedNumberOfCallsInHalfOpenState(10)
                                .build());
    }

    public Mono<String> post(final Object request) {
        log.atInfo()
                .setMessage("HTTP Webhook client request")
                .addKeyValue("name", NAME)
                .addKeyValue("request", request)
                .log();

        return this.webClient
                .post()
                .uri(POST_PATH)
                .bodyValue(request)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
                .onErrorResume(this::defaultErrorHandler);
    }

    public Mono<String> postWithCircuitBreaker(final Object request) {
        log.atInfo()
                .setMessage("HTTP Webhook client request with CircuitBreaker")
                .addKeyValue("name", NAME)
                .addKeyValue("request", request)
                .log();

        return this.webClient
                .post()
                .uri(POST_PATH)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .transformDeferred(CircuitBreakerOperator.of(this.defaultCircuitBreaker));
    }

    private Mono<String> defaultErrorHandler(final Throwable ex) {
        log.atWarn()
                .setMessage("HTTP Webhook client error")
                .addKeyValue("name", NAME)
                .addKeyValue("message", getErrorMessage(ex))
                .setCause(ex)
                .log();

        return Mono.empty();
    }
}
