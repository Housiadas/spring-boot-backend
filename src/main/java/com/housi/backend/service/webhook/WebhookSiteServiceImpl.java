package com.housi.backend.service.webhook;

import com.housi.backend.clients.http.WebhookSiteHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookSiteServiceImpl implements WebhookSiteService {

  private final WebhookSiteHttpClient client;

  @Override
  public Mono<String> post(final Object request) {
    // Deserialization (if needed) is done at service level
    return this.client.post(request).map(response -> response);
  }

  @Override
  public Mono<String> postWithCircuitBreaker(final Object request) {
    return this.client.postWithCircuitBreaker(request).map(response -> response);
  }
}
