package com.distributed.rateLimit.filter;

import com.distributed.rateLimit.service.RateLimiterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;

    public RateLimitFilter(
            RateLimiterService rateLimiterService) {

        this.rateLimiterService = rateLimiterService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String userId =
                request.getHeader("X-User-Id");

        if (userId == null || userId.isBlank()) {

            userId =
                    request.getRemoteAddr();
        }

        rateLimiterService.checkRateLimit(userId);

        filterChain.doFilter(request, response);
    }
}