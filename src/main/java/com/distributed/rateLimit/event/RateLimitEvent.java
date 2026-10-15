package com.distributed.rateLimit.event;

import java.time.Instant;

public record RateLimitEvent(
        String userId,
        String requestPath,
        boolean allowed,
        Instant timestamp
) {
}