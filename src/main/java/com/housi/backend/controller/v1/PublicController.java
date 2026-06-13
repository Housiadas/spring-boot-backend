package com.housi.backend.controller.v1;

import com.housi.backend.constant.AppUrls;
import com.housi.backend.service.webhook.WebhookSiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(PublicController.BASE_URL)
@RequiredArgsConstructor
public class PublicController {
  public static final String BASE_URL = AppUrls.V1_PUBLIC;

  private final WebhookSiteService webhookSiteService;

  @GetMapping("/call-external-api")
  @ResponseStatus(HttpStatus.OK)
  public Mono<String> callExternalAPI() {
    return this.webhookSiteService.post(Map.of());
  }

  @GetMapping("/call-external-api-with-cb")
  @ResponseStatus(HttpStatus.OK)
  public Mono<String> callExternalAPIWithCircuitBreaker() {
    return this.webhookSiteService.postWithCircuitBreaker(Map.of());
  }
}
