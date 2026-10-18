
package com.driveease.gateway.exception;

import java.time.LocalDateTime;

public class ApiError {

    private final String message;
    private final LocalDateTime timestamp;

    public ApiError(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
