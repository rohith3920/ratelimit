
package com.distributed.rateLimit.service;

import com.distributed.rateLimit.event.RateLimitEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
public class RateLimitEventProducer {

    private static final Logger log =
            LoggerFactory.getLogger(RateLimitEventProducer.class);

    private static final String TOPIC = "rate-limit-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public RateLimitEventProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishEvent(RateLimitEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(
                    TOPIC,
                    event.userId(),
                    message
            ).whenComplete((result, exception) -> {
                if (exception != null) {
                    log.error("Failed to publish rate-limit event",
                            exception);
                } else {
                    log.debug("Rate-limit event published for user {}",
                            event.userId());
                }
            });

        }  catch (JacksonException e) {
        log.error("Failed to serialize rate-limit event", e);
    }
    }
}
