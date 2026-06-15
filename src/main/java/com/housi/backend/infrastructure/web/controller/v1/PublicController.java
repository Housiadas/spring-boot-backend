package com.housi.backend.infrastructure.web.controller.v1;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.housi.backend.constant.AppUrls;
import com.housi.backend.usecase.webhook.WebhookSiteUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping(PublicController.BASE_URL)
@RequiredArgsConstructor
public class PublicController {
    public static final String BASE_URL = AppUrls.V1_PUBLIC;

    private final WebhookSiteUseCase webhookSiteUseCase;

    @GetMapping("/call-external-api")
    @ResponseStatus(HttpStatus.OK)
    public Mono<String> callExternalAPI() {
        return this.webhookSiteUseCase.post(Map.of());
    }

    @GetMapping("/call-external-api-with-cb")
    @ResponseStatus(HttpStatus.OK)
    public Mono<String> callExternalAPIWithCircuitBreaker() {
        return this.webhookSiteUseCase.postWithCircuitBreaker(Map.of());
    }
}
