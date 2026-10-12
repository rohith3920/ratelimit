package com.distributed.rateLimit.service;

import com.distributed.rateLimit.exception.RateLimitExceedException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final int REQUEST_LIMIT = 10;

    private static final Duration WINDOW =
            Duration.ofSeconds(20);

    public RateLimiterService(
            RedisTemplate<String, String> redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    public void checkRateLimit(String userId) {

        String key = "rate_limit:" + userId;

        Long requestCount =
                redisTemplate.opsForValue().increment(key);

        System.out.println(requestCount + "requestCount of the request");
        if (requestCount == null) {
            throw new RuntimeException(
                    "Unable to check rate limit"
            );
        }


        if (requestCount == 1) {

            redisTemplate.expire(
                    key,
                    WINDOW
            );
        }

        if (requestCount > REQUEST_LIMIT) {

            throw new RateLimitExceedException(
                    "Rate limit exceeded. " +
                            "Maximum " +
                            REQUEST_LIMIT +
                            " requests per second allowed."
            );
        }
    }
}