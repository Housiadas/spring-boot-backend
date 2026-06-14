package com.housi.backend.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.housi.backend.filter.HttpRequestLoggingFilter;
import jakarta.servlet.FilterChain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.event.KeyValuePair;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

class HttpRequestLoggingFilterTest {

    HttpRequestLoggingFilter filter;
    Logger httpLogger;
    ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        filter = new HttpRequestLoggingFilter();
        httpLogger = (Logger) LoggerFactory.getLogger("HTTP");
        appender = new ListAppender<>();
        appender.start();
        httpLogger.addAppender(appender);
        httpLogger.setLevel(Level.INFO);
    }

    @AfterEach
    void tearDown() {
        httpLogger.detachAppender(appender);
        MDC.clear();
    }

    @Test
    void logsStructuredEventForRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/users/current");
        request.setQueryString("verbose=true");
        request.setRemoteAddr("10.0.0.1");
        request.addHeader("User-Agent", "JUnit/1.0");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(200);

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(appender.list).hasSize(1);
        ILoggingEvent event = appender.list.get(0);
        assertThat(event.getLevel()).isEqualTo(Level.INFO);
        assertThat(event.getMessage()).isEqualTo("http.request");
        // Message must NOT carry parameters interpolated — everything is in key-values.
        assertThat(event.getFormattedMessage()).isEqualTo("http.request");

        Map<String, Object> kv = toMap(event.getKeyValuePairs());
        assertThat(kv)
                .containsEntry("event", "http.request")
                .containsEntry("request.method", "GET")
                .containsEntry("request.path", "/api/v1/users/current")
                .containsEntry("request.query", "verbose=true")
                .containsEntry("response.status", 200)
                .containsEntry("remote.addr", "10.0.0.1")
                .containsEntry("user.agent", "JUnit/1.0")
                .containsKey("durationMs");
        assertThat((Long) kv.get("durationMs")).isGreaterThanOrEqualTo(0L);
    }

    @Test
    void setsAndClearsRequestIdInMdc() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/ping");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String[] captured = new String[1];
        FilterChain chain =
                (req, res) -> captured[0] = MDC.get(HttpRequestLoggingFilter.REQUEST_ID);

        filter.doFilter(request, response, chain);

        assertThat(captured[0]).isNotBlank();
        assertThat(MDC.get(HttpRequestLoggingFilter.REQUEST_ID)).isNull();
    }

    @Test
    void clearsRequestIdEvenWhenChainThrows() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/boom");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain =
                (req, res) -> {
                    throw new RuntimeException("boom");
                };

        try {
            filter.doFilter(request, response, chain);
        } catch (Exception ignored) {
        }

        assertThat(MDC.get(HttpRequestLoggingFilter.REQUEST_ID)).isNull();
        assertThat(appender.list).hasSize(1);
        assertThat(appender.list.get(0).getMessage()).isEqualTo("http.request");
    }

    @Test
    void skipsActuatorAndDocsPaths() throws Exception {
        for (String path :
                List.of("/actuator/health", "/docs", "/v3/api-docs", "/swagger-ui/index.html")) {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(request, response, new MockFilterChain());
        }
        assertThat(appender.list).isEmpty();
    }

    private static Map<String, Object> toMap(List<KeyValuePair> pairs) {
        if (pairs == null) {
            return Map.of();
        }
        return pairs.stream().collect(Collectors.toMap(p -> p.key, p -> p.value));
    }
}
