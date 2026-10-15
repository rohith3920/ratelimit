
package com.distributed.rateLimit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RateLimitEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(RateLimitEventConsumer.class);

    @KafkaListener(topics = "rate-limit-events")
    public void consumeEvent(String message) {

        log.info("Received Kafka event: {}", message);

        // Process the rate-limit event here
        log.info("Rate-limit event processed successfully");
    }
}
