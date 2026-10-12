package com.distributed.rateLimit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceedException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceeded(
            RateLimitExceedException exception) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "Too Many Requests",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(errorResponse);
    }
}
