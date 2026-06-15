package com.housi.backend.infrastructure.web.actuator;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Endpoint(id = "preStopHook")
class WebMvcPreStopHookEndpoint {

    @ReadOperation
    public void preStopHook(@Selector final long delayInMillis) throws InterruptedException {
        log.info("[preStopHook] received signal to sleep for {}ms", delayInMillis);
        Thread.sleep(delayInMillis);
    }
}
