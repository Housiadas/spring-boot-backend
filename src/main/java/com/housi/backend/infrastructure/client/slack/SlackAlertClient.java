package com.housi.backend.infrastructure.client.slack;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SlackAlertClient {

    private final String url;
    private final String channel;
    private final String env;

    public SlackAlertClient(
            @Value("${slack.env}") final String env,
            @Value("${slack.channels.api-alert.url}") final String url,
            @Value("${slack.channels.api-alert.name}") final String channel) {
        this.env = env;
        this.url = url;
        this.channel = channel;
    }

    @Async
    public void notify(final String message) {
        try {
            final WebhookResponse response =
                    Slack.getInstance().send(this.url, buildBodyMessage(message));

            if (response.getCode() != 200) {
                log.atWarn()
                        .setMessage("[SLACK][ERROR] failed to send message")
                        .addKeyValue("status", response.getCode())
                        .addKeyValue("body", response.getBody())
                        .log();
            }
        } catch (final Exception ex) {
            log.atError()
                    .setMessage("[SLACK][ERROR] An error has occurred when sending error message")
                    .setCause(ex)
                    .log();
        }
    }

    private String buildBodyMessage(final String message) {
        final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        final String timestamp =
                formatter.format(
                        OffsetDateTime.now().atZoneSameInstant(ZoneId.of("Europe/Athens")));

        return format(
                """
                {
                  "channel": "#%s",
                  "text": "*error*",
                  "blocks": [
                    {
                      "type": "section",
                      "fields": [
                        {
                          "type": "mrkdwn",
                          "text": "*Team:*\\n API"
                        },
                        {
                          "type": "mrkdwn",
                          "text": "*When:*\\n%s"
                        },
                        {
                          "type": "mrkdwn",
                          "text": "* Env:*\\n%s"
                        },
                        {
                          "type": "mrkdwn",
                          "text": "*Reason:*\\n%s."
                        }
                      ]
                    },
                    %s
                  ]
                }
                """,
                this.channel,
                timestamp,
                this.env,
                new String(JsonStringEncoder.getInstance().quoteAsString(message)),
                buildTraceAndLogButtons());
    }

    private String buildTraceAndLogButtons() {
        final String logUrl = "https://example.com";
        final String traceUrl = "https://example.com";

        return isNotBlank(logUrl)
                ? format(
                        """
                        {
                              "type": "actions",
                              "elements": [
                                {
                                  "type": "button",
                                  "url": "%s",
                                  "style": "primary",
                                  "text": {
                                    "type": "plain_text",
                                    "text": "Logs",
                                    "emoji": true
                                  },
                                  "value": "btn-logs",
                                  "action_id": "logUrl"
                                },
                                {
                                  "type": "button",
                                  "url": "%s",
                                  "text": {
                                    "type": "plain_text",
                                    "text": "Traces",
                                    "emoji": true
                                  },
                                  "value": "btn-trace",
                                  "action_id": "traceUrl"
                                }
                              ]
                            }
                        """,
                        logUrl, traceUrl)
                : StringUtils.EMPTY;
    }
}
