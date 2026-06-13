package com.housi.backend.controller.actuator;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * WebMvcPreStopHookEndpoint:
 *
 * <p>This API is used to create a preStop hook for kubernetes (kubelet) to await a certain
 * delayInMillis before sending the SIGTERM signal. It allows 0 downtime deployment.
 */
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
